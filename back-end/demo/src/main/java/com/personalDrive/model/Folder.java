package com.personalDrive.model;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(
    name = "folder",
    uniqueConstraints = @UniqueConstraint(
        name = "folder_owner_id_parent_id_name_key",
        columnNames = {"owner_id", "parent_id", "name"}
    ),
    indexes = {
        @Index(name = "idx_folder_parent", columnList = "parent_id")
    }
)
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @OneToMany(mappedBy = "parent")
    private Set<Folder> children = new LinkedHashSet<>();

    // @Column(name = "is_trashed", nullable = false)
    // private boolean trashed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ---------- lifecycle timestamps ---------- */
    @PrePersist
    void onCreate() {
        var now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    /* ---------- getters/setters ---------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Folder getParent() { return parent; }
    public void setParent(Folder parent) { this.parent = parent; }

    public Set<Folder> getChildren() { return children; }
    public void setChildren(Set<Folder> children) { this.children = children; }

    // public boolean isTrashed() { return trashed; }
    // public void setTrashed(boolean trashed) { this.trashed = trashed; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
