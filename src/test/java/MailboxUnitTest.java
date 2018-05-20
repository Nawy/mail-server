import com.mega.mailserver.model.domain.Chat;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.Mailbox;
import com.mega.mailserver.repository.MailboxRepository;
import com.mega.mailserver.service.MailboxService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MailboxService.class)
public class MailboxUnitTest {

    @MockBean
    MailboxRepository repository;

    @Autowired
    MailboxService mailboxService;

    private final String USER_NAME = "testUser";
    private final String CHAT_NAME_1 = "chat1@example.com";
    private final String CHAT_NAME_2 = "chat2@example.com";
    private final String CHAT_NAME_3 = "chat3@example.com";

    private final String TEXT_1 = "Text1";
    private final String TEXT_2 = "Text2";
    private final String TEXT_3 = "Text3";


    @Test
    public void getChatFromSpamUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .contacts(Collections.emptyList())
                .spam(Arrays.asList(
                        createChat(CHAT_NAME_1),
                        createChat(CHAT_NAME_2),
                        createChat(CHAT_NAME_3)
                ))
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        Collection<Letter> letter2 = mailboxService.getChat(USER_NAME, CHAT_NAME_2);
        assertThat(letter2).isNotEmpty();
        assertThat(letter2).extracting("address").containsOnly(CHAT_NAME_2);
        assertThat(letter2).extracting("text").contains(TEXT_1, TEXT_2, TEXT_3);
    }

    @Test
    public void getChatFromContactsUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .contacts(Arrays.asList(
                        createChat(CHAT_NAME_1),
                        createChat(CHAT_NAME_2),
                        createChat(CHAT_NAME_3)
                ))
                .spam(Collections.emptyList())
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        Collection<Letter> letter2 = mailboxService.getChat(USER_NAME, CHAT_NAME_2);
        assertThat(letter2).isNotEmpty();
        assertThat(letter2).extracting("address").containsOnly(CHAT_NAME_2);
        assertThat(letter2).extracting("text").contains(TEXT_1, TEXT_2, TEXT_3);
    }

    @Test
    public void getChatEmptyUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .contacts(Collections.emptyList())
                .spam(Collections.emptyList())
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        Collection<Letter> letter2 = mailboxService.getChat(USER_NAME, CHAT_NAME_2);
        assertThat(letter2).isEmpty();
    }

    private Chat createChat(final String chatName) {
        return Chat.builder()
                .name(chatName)
                .messages(Arrays.asList(
                        Letter.builder()
                                .address(chatName)
                                .text(TEXT_1)
                                .build(),
                        Letter.builder()
                                .address(chatName)
                                .text(TEXT_2)
                                .build(),
                        Letter.builder()
                                .address(chatName)
                                .text(TEXT_3)
                                .build()
                ))
                .build();
    }
}
