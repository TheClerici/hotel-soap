package com.choice.webservice.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "amenities")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Amenity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amenity_id")
    private Long amenityId;

    @Column(name = "amenity_name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, mappedBy = "amenities")
    Set<Hotel> hotels;

    /* public void deleteHotelFromSet(Hotel hotel) {
        hotels.remove(hotel);
    } */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Amenity amenity = (Amenity) o;
        return amenityId != null && Objects.equals(amenityId, amenity.amenityId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}