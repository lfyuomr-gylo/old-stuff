package com.github.lfyuomr.gylo.kango.server.proto.messages.server2client;


import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.CCPoweredKey;
import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.ClientToServerProtoMessage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.function.Predicate;

@XmlRootElement(name = "CC_POWER_KEY")
public class CCPowerKey extends ServerToClientProtoMessage implements ServerQuestioner {
    @XmlElement
    public int iteration;

    @XmlElement
    public int titleHash;

    @XmlElement
    public BigInteger value;

    @XmlElement
    public BigInteger modulo;

    public CCPowerKey() {
    }

    public CCPowerKey(int iteration, int titleHash, BigInteger value, BigInteger modulo) {
        this.iteration = iteration;
        this.titleHash = titleHash;
        this.value = value;
        this.modulo = modulo;
    }

    @Override
    public Predicate<ClientToServerProtoMessage> getResponseRecognizer() {
        return mes -> {
            if (mes instanceof CCPoweredKey) {
                return iteration == ((CCPoweredKey) mes).iteration &&
                        titleHash == ((CCPoweredKey) mes).titleHash;
            }
            return false;
        };
    }
}
