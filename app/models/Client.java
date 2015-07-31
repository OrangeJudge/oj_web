package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * System's client.
 */
@Entity
public class Client extends Model {
    @Id
    public int id;
    public String name;
    public String secret;
}
