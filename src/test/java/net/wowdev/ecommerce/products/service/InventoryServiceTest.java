package net.wowdev.ecommerce.products.service;

import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import net.wowdev.ecommerce.domain.entity.InventoryChangeType;
import net.wowdev.ecommerce.domain.entity.InventoryEntity;
import net.wowdev.ecommerce.products.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    private static final UUID ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID PRODUCT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock private InventoryRepository repository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @Test
    void createsAnAppendOnlyInventoryEvent() {
        final InventoryDTO request = inventory(null);
        when(repository.save(any(InventoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        final InventoryService service = new InventoryServiceImpl(repository, eventPublisher);

        final InventoryDTO result = service.create(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(10);
        verify(eventPublisher).publishEvent(result);
    }

    @Test
    void readsInventoryEvents() {
        final InventoryEntity entity = new InventoryEntity(ID, null, null, 10, 10,
                InventoryChangeType.INVENTORY_INCREASE, null, null);
        when(repository.findByIdAndProductEntity_Id(ID, PRODUCT_ID)).thenReturn(Optional.of(entity));
        when(repository.findAllByProductEntity_Id(org.mockito.ArgumentMatchers.eq(PRODUCT_ID),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        final InventoryService service = new InventoryServiceImpl(repository, eventPublisher);

        assertThat(service.findById(ID, PRODUCT_ID).getId()).isEqualTo(ID);
        assertThat(service.findAll(PRODUCT_ID, 0, 20)).hasSize(1);
    }

    @Test
    void rejectsMissingInventoryEvent() {
        when(repository.findByIdAndProductEntity_Id(ID, PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new InventoryServiceImpl(repository, eventPublisher).findById(ID, PRODUCT_ID))
                .isInstanceOf(InventoryNotFoundException.class);
    }

    @Test
    void deletesInventoryEventForRollback() {
        when(repository.existsById(ID)).thenReturn(true);
        final InventoryService service = new InventoryServiceImpl(repository, eventPublisher);

        service.delete(ID);

        verify(repository).deleteById(ID);
    }

    @Test
    void rejectsRollbackOfMissingInventoryEvent() {
        when(repository.existsById(ID)).thenReturn(false);
        final InventoryService service = new InventoryServiceImpl(repository, eventPublisher);

        assertThatThrownBy(() -> service.delete(ID))
                .isInstanceOf(InventoryNotFoundException.class);
        verifyNoMoreInteractions(repository);
    }

    private static InventoryDTO inventory(final UUID id) {
        return new InventoryDTO(id, null, null, 10, 10,
                InventoryChangeType.INVENTORY_INCREASE, null, null);
    }
}
