package com.acme.iamcafelab.shared.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class AuditableAbstractAggregateRoot<T extends AbstractAggregateRoot<T>> extends AbstractAggregateRoot<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;

    /**
     * Registers a domain event.
     *
     * @param event the domain event to register
     */
    public void addDomainEvent(Object event) {
        super.registerEvent(event);
    }

    /**
     * Gets the current domain events.
     *
     * @return the registered domain events
     */
    public List<Object> getDomainEvents() {
        return List.copyOf(super.domainEvents());
    }

    /**
     * Clears the registered domain events.
     */
    public void clearEvents() {
        super.clearDomainEvents();
    }
}