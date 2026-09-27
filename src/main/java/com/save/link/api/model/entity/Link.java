package com.save.link.api.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tb_links", schema = "svl")
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_link")
    private Long id;

    @Column(name = "ds_titulo")
    private String descTitle;

    @Column(name = "ds_url")
    private String descURL;

    @Column(name = "dt_created")
    private LocalDate dataCreated = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    public Link() {}

    public Link(User user, String descTitle, String descURL) {
        this.user = user;
        this.descTitle = descTitle;
        this.descURL = descURL;
        this.dataCreated = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescTitle() {
        return descTitle;
    }

    public void setDescTitle(String descTitle) {
        this.descTitle = descTitle;
    }

    public String getDescURL() {
        return descURL;
    }

    public void setDescURL(String descURL) {
        this.descURL = descURL;
    }

    public LocalDate getDataCreated() {
        return dataCreated;
    }

    public void setDataCreated(LocalDate dataCreated) {
        this.dataCreated = dataCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
