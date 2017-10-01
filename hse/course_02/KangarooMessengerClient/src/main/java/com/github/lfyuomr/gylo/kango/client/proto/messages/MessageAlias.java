package com.github.lfyuomr.gylo.kango.client.proto.messages;

import com.github.lfyuomr.gylo.kango.client.proto.messages.client2server.*;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.*;

@SuppressWarnings("WeakerAccess")
public enum MessageAlias {
    AUTHORIZE(Authorize.class),
    CC_CONFIRMATION(CCConfirmation.class),
    CC_POWERED_KEY(CCPoweredKey.class),
    CREATE_CONVERSATION(CreateConversation.class),
    REGISTER(Register.class),
    SEARCH_CONTACT(SearchContact.class),
    SEND_CHAT_MESSAGE(SendChatMessage.class),
    AUTHORIZATION_FAILED(AuthorizationFailed.class),
    AUTHORIZATION_SUCCEED(AuthorizationSucceeded.class),
    CC_CONFIRM(CCConfirm.class),
    CC_FAILED(CCFailed.class),
    CC_POWER_KEY(CCPowerKey.class),
    CC_SUCCEED(CCSucceeded.class),
    CHAT_MESSAGE_SENT(ChatMessageSent.class),
    CHAT_MESSAGE_SEND_FAILED(ChatMessageSendFailed.class),
    CONTACT_SEARCH_FAILED(ContactSearchFailed.class),
    CONTACT_SEARCH_RESULT(ContactSearchResult.class),
    INPUT_CHAT_MESSAGE(InputChatMessage.class),
    REGISTRATION_FAILED(RegistrationFailed.class),
    REGISTRATION_SUCCEED(RegistrationSucceeded.class);



    MessageAlias(Class type) {
        this.type = type;
    }

    public Class getType() {
        return type;
    }

    private Class type;
}
