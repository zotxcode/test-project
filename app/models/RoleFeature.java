package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;


/**
 * @author hendriksaragih
 */
@Entity
public class RoleFeature extends Model{

    @ManyToOne
    @JoinColumn(name = "feature_id", referencedColumnName = "id")
    @JsonIgnore
    public Feature feature;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @JsonIgnore
    public Role role;

    public int access;

    @javax.persistence.Transient
    public Long roleId;

    @javax.persistence.Transient
    public Long featureId;

    public boolean isView(){
        if(access%2 == 0)
            return true;
        else return false;
    }

    public boolean isAdd(){
        if(access%3 == 0)
            return true;
        else return false;
    }

    public boolean isEdit(){
        if(access%5 == 0)
            return true;
        else return false;
    }

    public boolean isDelete(){
        if(access%7 == 0)
            return true;
        else return false;
    }

    public static List<RoleFeature> getFeaturesByRole(Long id){
        String sql = "SELECT role_id, feature_id, access FROM role_feature " +
                "WHERE role_id = "+id+" " +
                "ORDER BY feature_id ASC";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("role_id", "roleId")
                .columnMapping("feature_id", "featureId")
                .columnMapping("access", "access")
                .create();
        com.avaje.ebean.Query<RoleFeature> query = Ebean.find(RoleFeature.class);
        query.setRawSql(rawSql);
        List<RoleFeature> resData = query.findList();

        return resData;
    }

    public static void setRoles(Role role, Feature... features){
        for (Feature feature: features){
            RoleFeature rf = new RoleFeature();
            rf.feature = feature;
            rf.role = role;
            rf.access = 210;
            rf.save();
        }
    }

    public static RoleFeature getFeaturesByRoleAndFeature(Long id, String featureKey){
        String sql = "SELECT rf.role_id, rf.feature_id, rf.access FROM role_feature rf " +
                "LEFT JOIN feature f on rf.feature_id=f.id " +
                "WHERE rf.role_id = "+id+" and f.key ='"+featureKey+"' " +
                "ORDER BY rf.feature_id ASC";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("rf.role_id", "roleId")
                .columnMapping("rf.feature_id", "featureId")
                .columnMapping("rf.access", "access")
                .create();
        com.avaje.ebean.Query<RoleFeature> query = Ebean.find(RoleFeature.class);
        query.setRawSql(rawSql);
        List<RoleFeature> resData = query.findList();
        if(resData.size() > 0 )
            return resData.get(0);
        else return null;
    }
}