import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(name = "city")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY , cascade = CascadeType.ALL, orphanRemoval = false)
    private List<State_City> state_city = new ArrayList<>();

    public List<State_City> getState_City() {
        return state_city;
    }

    public void setState_City(List<State_City> sc) {
        this.state_city = sc;
    }

    public void addState_City(State_City sc) {
        this.state_city.add(sc);
    }

    @Override
    public String toString() {
        return "City{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City c = (City) o;
        return Objects.equals(id, c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}