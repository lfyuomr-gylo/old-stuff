package com.github.lfyuomr.gylo.kango.client.proto.messages.client2server;

import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.CCConfirm;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.CCFailed;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.CCPowerKey;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.ServerToClientProtoMessage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
@XmlRootElement(name = "CC_POWERED_KEY")
public class CCPoweredKey extends ClientToServerProtoMessage implements ClientQuestioner {
    @XmlElement
    public int iteration;

    @XmlElement
    public int titleHash;

    @XmlElement
    public java.math.BigInteger value;

    public CCPoweredKey() {
    }

    public CCPoweredKey(int iteration, int titleHash, BigInteger value) {
        this.iteration = iteration;
        this.titleHash = titleHash;
        this.value = value;
    }

    @Override
    public Predicate<ServerToClientProtoMessage> getResponseRecognizer() {
        return mes -> {
            if (mes instanceof CCPowerKey) {
                return iteration == ((CCPowerKey) mes).iteration + 1 &&
                        titleHash == ((CCPowerKey) mes).titleHash;
            }
            else if (mes instanceof CCConfirm) {
                return iteration == ((CCConfirm) mes).iterationsNum - 1 &&
                        titleHash == ((CCConfirm) mes).titleHash;
            }
            else if (mes instanceof CCFailed) {
                return titleHash == ((CCFailed) mes).titleHash;
            }
            return false;
        };
    }
}
