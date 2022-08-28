package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
@EqualsAndHashCode
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Boolean available;

    @Column(name = "owner_id")
    private Long owner;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private ItemRequest request;
}
