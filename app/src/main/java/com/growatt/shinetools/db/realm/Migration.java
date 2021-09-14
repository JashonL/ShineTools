package com.growatt.shinetools.db.realm;


import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created：2019/1/4 on 15:01
 * Author:gaideng on dg
 * Description:realm 数据库版本升级
 */

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        String old = String.valueOf(oldVersion);
        RealmSchema schema = realm.getSchema();
        switch (old){
            case "0":dealVersion0(schema);oldVersion++;
            case "1":dealVersion1(schema);oldVersion++;
        }
    }

    /**
     * Fragment1ListBean 新增type字段
     * @param schema
     */
    private void dealVersion0(RealmSchema schema) {
        RealmObjectSchema fragment1ListBean = schema.get("Fragment1ListBean");
        fragment1ListBean.addField("type",int.class)
                .transform(obj -> {
                   obj.set("type",0);
                });
    }
    /**
     * Fragment1ListBean 新增type字段
     * @param schema
     */
    private void dealVersion1(RealmSchema schema) {
        RealmObjectSchema fragment1ListBean = schema.get("Fragment1ListBean");
        fragment1ListBean.addField("bdc2Soc",int.class)
                .addField("bdc1Soc",int.class)
                .transform(obj -> {
                   obj.set("bdc1Soc",0);
                   obj.set("bdc2Soc",0);
                });
    }
}
