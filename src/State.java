
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "state")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<State_City> state_city = new ArrayList<>();

    public List<State_City> getState_City() {
        return state_city;
    }

    public void setState_City(List<State_City> state_city) {
        this.state_city = state_city;
    }

    public void addState_City(State_City ts) {
        this.state_city.add(ts);
    }

    @Override
    public String toString() {
        return "State{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State s = (State) o;
        return Objects.equals(id, s.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}