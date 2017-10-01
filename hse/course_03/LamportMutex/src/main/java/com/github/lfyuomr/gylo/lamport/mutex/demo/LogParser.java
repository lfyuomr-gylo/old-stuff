package com.github.lfyuomr.gylo.lamport.mutex.demo;

import lombok.Getter;
import lombok.val;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

class LogParser {
    private final Scanner scan;

    @Getter private boolean ok;
    @Getter private String errorMessage;

    private int nodesNum;
    private final Map<Integer, Stack<Action>> states = new HashMap<>();

    LogParser(final String fileName) throws IOException {
        this.scan = new Scanner(new FileInputStream(fileName));

        try {
            this.nodesNum = extractNodesNum();
            parse();
            checkAfterLogEnds();
            this.ok = true;
        } catch (IllegalStateException e) {
            this.ok = false;
            errorMessage = e.getMessage();
        }
    }

    private void parse() {
        val pattern = Pattern.compile("mutex (?<num>(\\d)+): (?<action>(release)|(lock)|(acquire))");
        while (scan.hasNextLine()) {
            val lineMatch = pattern.matcher(scan.nextLine());
            if (lineMatch.matches()) {
                val num = parseInt(lineMatch.group("num"));
                val action = Action.valueOf(lineMatch.group("action").toUpperCase());
                processAction(num, action);
            }
        }
    }

    private void processAction(int num, Action action) {
        final Stack<Action> stack;
        if (states.containsKey(num)) {
            stack = states.get(num);
        } else {
            stack = new Stack<>();
            states.put(num, stack);
            final int statesSize = states.entrySet().size();
            if (statesSize > nodesNum) {
                throw new IllegalStateException("unexpected nodes num: " + statesSize);
            }
        }

        if (stack.isEmpty()) {
            if (action == Action.RELEASE) {
                throw new IllegalStateException("Process " + num +
                        " released mutex with no previous lock request or acquisition");
            } else if (action == Action.ACQUIRE) {
                throw new IllegalStateException("Process " + num +
                        " acquired lock with no previous lock request");
            } else {
                stack.push(action);
                return;
            }
        } else {
            val last = stack.peek();
            switch (action) {
                case ACQUIRE:
                    if (last != Action.LOCK) {
                        throw new IllegalStateException("Process " + num + " unexpectedly acquired lock after " + last
                                + ". Expected " + Action.LOCK);
                    }
                    stack.push(action);
                    break;
                case LOCK:
                    throw new IllegalStateException("Process " + num + " unexpectedly requested lock after " + last + ".");
                case RELEASE:
                    if (last != Action.ACQUIRE) {
                        throw new IllegalStateException("Process " + num + " unexpectedly released lock after " + last +
                        ". Expected " + Action.ACQUIRE);
                    }
                    stack.pop(); // acquire
                    stack.pop(); // lock
                    break;
            }
        }
    }

    private void checkAfterLogEnds() {
        val stacks = states.values();
        if (stacks.size() != nodesNum) {
            throw new IllegalStateException("Unexpected number of nodes found in log: " + stacks.size() +
                    ". Expected " + nodesNum);
        }
    }

    private int extractNodesNum() {
        val pattern = Pattern.compile("mutex ((\\d)+): created new mutex on port ((\\d)+) with " +
                "(?<nodesNum>(\\d)+) nodes in configuration.");
        while (scan.hasNextLine()) {
            val lineMatch = pattern.matcher(scan.nextLine());
            if (lineMatch.matches()) {
                return parseInt(lineMatch.group("nodesNum"));
            }
        }
        throw new IllegalStateException("No configuration line in log.");
    }

    private enum Action {
        LOCK,
        ACQUIRE,
        RELEASE,
        ;
    }
}
