package com.choice.webservice.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.CascadeType.PERSIST;

@Entity
@Table(name = "hotels")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Hotel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long hotelId;
    @Column(name = "hotel_name")
    private String name;
    @Column(name = "hotel_address")
    private String address;
    @Column(name = "hotel_rating")
    private int rating;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            PERSIST
    })
    @JoinTable(
            name = "hotel_amenities",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    Set<Amenity> amenities;

    public Hotel(Long hotelId, String name, String address, int rating) {
        this.hotelId = hotelId;
        this.name = name;
        this.address = address;
        this.rating = rating;
    }

/*    public void deleteAmenityFromSet(Amenity amenity) {
        amenities.remove(amenity);
        amenity.deleteHotelFromSet(this);
    } */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Hotel hotel = (Hotel) o;
        return hotelId != null && Objects.equals(hotelId, hotel.hotelId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}






