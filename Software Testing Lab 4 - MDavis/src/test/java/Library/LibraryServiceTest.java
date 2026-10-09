package Library;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private EmailProvider emailProvider;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private LibraryService libraryService;


    // TC01 - Null resource ID
    @Test
    void checkoutResource_nullResourceId_returnsFalse() throws Exception {

        boolean result = libraryService.checkoutResource(
                null,
                "member@example.com"
        );

        assertFalse(result);

        verifyNoInteractions(resourceRepository, emailProvider);
    }


    // TC02 - Resource is unavailable
    @Test
    void checkoutResource_unavailableResource_returnsFalse()
            throws Exception {

        UUID resourceId = UUID.randomUUID();

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(false);

        boolean result = libraryService.checkoutResource(
                resourceId,
                "member@example.com"
        );

        assertFalse(result);

        verify(resourceRepository)
                .isResourceAvailable(resourceId);

        verify(resourceRepository, never())
                .updateStatus(any(UUID.class), anyBoolean());

        verifyNoInteractions(emailProvider);
    }


    // TC03 - Successful checkout
    @Test
    void checkoutResource_successfulCheckout_returnsTrue()
            throws Exception {

        UUID resourceId = UUID.randomUUID();
        String email = "member@example.com";

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenReturn(true);

        when(emailProvider.sendEmail(
                eq(email),
                eq("Resource ID: " + resourceId + " checked out.")
        )).thenReturn(true);

        boolean result = libraryService.checkoutResource(
                resourceId,
                email
        );

        assertTrue(result);

        verify(resourceRepository)
                .isResourceAvailable(resourceId);

        verify(resourceRepository)
                .updateStatus(resourceId, false);

        verify(emailProvider)
                .sendEmail(
                        eq(email),
                        eq("Resource ID: " + resourceId + " checked out.")
                );
    }


    // TC04 - Database update returns false
    @Test
    void checkoutResource_updateFails_throwsDatabaseFailureException()
            throws Exception {

        UUID resourceId = UUID.randomUUID();

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenReturn(false);

        assertThrows(
                DatabaseFailureException.class,
                () -> libraryService.checkoutResource(
                        resourceId,
                        "member@example.com"
                )
        );

        verify(resourceRepository)
                .isResourceAvailable(resourceId);

        verify(resourceRepository)
                .updateStatus(resourceId, false);

        verifyNoInteractions(emailProvider);
    }


    // TC05 - Email provider returns false
    @Test
    void checkoutResource_emailFails_throwsEmailFailureException()
            throws Exception {

        UUID resourceId = UUID.randomUUID();
        String email = "member@example.com";

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenReturn(true);

        when(emailProvider.sendEmail(
                eq(email),
                eq("Resource ID: " + resourceId + " checked out.")
        )).thenReturn(false);

        assertThrows(
                EmailFailureException.class,
                () -> libraryService.checkoutResource(
                        resourceId,
                        email
                )
        );

        verify(resourceRepository)
                .isResourceAvailable(resourceId);

        verify(resourceRepository)
                .updateStatus(resourceId, false);

        verify(emailProvider)
                .sendEmail(
                        eq(email),
                        eq("Resource ID: " + resourceId + " checked out.")
                );
    }


    // TC06 - Email provider throws EmailFailureException
    @Test
    void checkoutResource_emailProviderThrowsException()
            throws Exception {

        UUID resourceId = UUID.randomUUID();
        String email = "member@example.com";

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenReturn(true);

        doThrow(
                new EmailFailureException("Email provider unavailable")
        ).when(emailProvider).sendEmail(
                eq(email),
                eq("Resource ID: " + resourceId + " checked out.")
        );

        assertThrows(
                EmailFailureException.class,
                () -> libraryService.checkoutResource(
                        resourceId,
                        email
                )
        );

        verify(resourceRepository)
                .isResourceAvailable(resourceId);

        verify(resourceRepository)
                .updateStatus(resourceId, false);

        verify(emailProvider)
                .sendEmail(
                        eq(email),
                        eq("Resource ID: " + resourceId + " checked out.")
                );
    }


    // TC07 - Repository throws DatabaseFailureException
    @Test
    void checkoutResource_repositoryThrowsDatabaseException()
            throws Exception {

        UUID resourceId = UUID.randomUUID();

        when(resourceRepository.isResourceAvailable(resourceId))
                .thenReturn(true);

        when(resourceRepository.updateStatus(resourceId, false))
                .thenThrow(
                        new DatabaseFailureException(
                                "Database unavailable"
                        )
                );

        assertThrows(
                DatabaseFailureException.class,
                () -> libraryService.checkoutResource(
                        resourceId,
                        "member@example.com"
                )
        );

        verify(resourceRepository)
                .isResourceAvailable(resourceId);

        verify(resourceRepository)
                .updateStatus(resourceId, false);

        verifyNoInteractions(emailProvider);
    }
}