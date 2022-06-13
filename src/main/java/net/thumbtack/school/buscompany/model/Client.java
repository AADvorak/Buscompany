package net.thumbtack.school.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Client extends User {

    @Column
    private String email;

    @Column
    private String phone;

}
