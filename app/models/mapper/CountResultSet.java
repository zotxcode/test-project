package models.mapper;

import com.avaje.ebean.annotation.Sql;

import javax.persistence.Entity;

@Entity
@Sql
public class CountResultSet {
    public int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}