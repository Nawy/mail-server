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

import java.time.LocalDateTime;
import java.util.*;

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
    private final String CHAT_NAME_4 = "chat3@example.com";

    private final String TEXT_1 = "Text1";
    private final String TEXT_2 = "Text2";
    private final String TEXT_3 = "Text3";
    private final String TEXT_4 = "Text3";


    @Test
    public void getChatFromSpamUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
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
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        Collection<Letter> letter2 = mailboxService.getChat(USER_NAME, CHAT_NAME_2);
        assertThat(letter2).isEmpty();
    }

    @Test
    public void getSpamChatsUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .contacts(Collections.singletonList(
                        createChat(CHAT_NAME_3)
                ))
                .spam(Arrays.asList(
                        createChat(CHAT_NAME_1),
                        createChat(CHAT_NAME_2)
                ))
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        List<Chat> chats = mailboxService.getChats(USER_NAME, true);
        assertThat(chats).extracting("name").containsOnly(CHAT_NAME_1, CHAT_NAME_2);
    }

    @Test
    public void getContactsChatsUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .contacts(Collections.singletonList(
                        createChat(CHAT_NAME_3)
                ))
                .spam(Arrays.asList(
                        createChat(CHAT_NAME_1),
                        createChat(CHAT_NAME_2)
                ))
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        List<Chat> chats = mailboxService.getChats(USER_NAME, false);
        assertThat(chats).extracting("name").contains(CHAT_NAME_3);
    }

    @Test
    public void getSpamChatsEmptyUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .contacts(Collections.singletonList(
                        createChat(CHAT_NAME_3)
                ))
                .spam(Collections.emptyList())
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        List<Chat> chats = mailboxService.getChats(USER_NAME, true);
        assertThat(chats).isEmpty();
    }

    @Test
    public void getContactsChatsEmptyUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        List<Chat> chats = mailboxService.getChats(USER_NAME, false);
        assertThat(chats).isEmpty();
    }

    @Test
    public void getSpamChatNamesUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .contacts(Collections.singletonList(
                        createChat(CHAT_NAME_3)
                ))
                .spam(Arrays.asList(
                        createChat(CHAT_NAME_1),
                        createChat(CHAT_NAME_2)
                ))
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        Set<String> names = mailboxService.getChatNames(USER_NAME, true);
        assertThat(names).contains(CHAT_NAME_1, CHAT_NAME_2);
    }

    @Test
    public void getContactsChatNamesUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .contacts(Collections.singletonList(
                        createChat(CHAT_NAME_3)
                ))
                .spam(Arrays.asList(
                        createChat(CHAT_NAME_1),
                        createChat(CHAT_NAME_2)
                ))
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        Set<String> names = mailboxService.getChatNames(USER_NAME, false);
        assertThat(names).contains(CHAT_NAME_3);
    }

    @Test
    public void getContactChatNamesEmptyUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .spam(Arrays.asList(
                        createChat(CHAT_NAME_1),
                        createChat(CHAT_NAME_2)
                ))
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        Set<String> names = mailboxService.getChatNames(USER_NAME, false);
        assertThat(names).isEmpty();
    }

    @Test
    public void getSpamChatNamesEmptyUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        Set<String> names = mailboxService.getChatNames(USER_NAME, true);
        assertThat(names).isEmpty();
    }

    @Test
    public void putLetterToSpamUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        final LocalDateTime deliveryTime = LocalDateTime.now();
        final Letter letter = Letter.builder()
                .address(CHAT_NAME_4)
                .text(TEXT_4)
                .deliveryTime(deliveryTime)
                .build();

        final Mailbox resultMailbox = mailboxService.putLetterToMailbox(letter, USER_NAME);
        final Chat chat = mailboxService.getChatByAddress(CHAT_NAME_4, resultMailbox.getSpam()).orElse(null);

        assertThat(chat).isNotNull();
        assertThat(chat.getAmountNew()).isEqualTo(1);
        assertThat(chat.getLastDeliveryDate()).isEqualTo(deliveryTime);
        assertThat(chat.getMessages()).isNotEmpty();
        assertThat(chat.getMessages()).extracting("address").contains(CHAT_NAME_4);
        assertThat(chat.getMessages()).extracting("text").contains(TEXT_4);
    }

    @Test
    public void putLetterToContactsUnitTest() {
        final Mailbox mailbox = Mailbox.builder()
                .userName(USER_NAME)
                .contacts(
                        new ArrayList<>(
                                Arrays.asList(
                                        createChat(CHAT_NAME_1),
                                        createChat(CHAT_NAME_2),
                                        createChat(CHAT_NAME_4)
                                )
                        ))
                .build();
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(mailbox));

        final LocalDateTime deliveryTime = LocalDateTime.now();
        final Letter letter = Letter.builder()
                .address(CHAT_NAME_4)
                .text(TEXT_4)
                .deliveryTime(deliveryTime)
                .build();

        final Mailbox resultMailbox = mailboxService.putLetterToMailbox(letter, USER_NAME);
        final Chat chat = mailboxService.getChatByAddress(CHAT_NAME_4, resultMailbox.getContacts()).orElse(null);

        assertThat(chat).isNotNull();
        assertThat(chat.getAmountNew()).isEqualTo(1);
        assertThat(chat.getLastDeliveryDate()).isEqualTo(deliveryTime);
        assertThat(chat.getMessages()).isNotEmpty();
        assertThat(chat.getMessages()).extracting("address").contains(CHAT_NAME_4);
        assertThat(chat.getMessages()).extracting("text").contains(TEXT_1, TEXT_2, TEXT_3, TEXT_4);
    }

    private Chat createChat(final String chatName) {
        return Chat.builder()
                .name(chatName)
                .messages(
                        new ArrayList<>(
                                Arrays.asList(
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
                                )
                        )
                )
                .build();
    }
}
