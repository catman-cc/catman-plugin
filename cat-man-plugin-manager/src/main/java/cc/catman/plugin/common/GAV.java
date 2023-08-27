package cc.catman.plugin.common;

import lombok.*;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GAV {
    /**
     * 插件所属组织
     */
    protected  String group;
    /**
     * 插件名称
     */
    protected String name;

    /**
     * 插件的版本信息
     */
    protected String version;

    public boolean Match(String group,String name,String version){
        return Match(GAV.builder().group(group).name(name).version(version).build());
    }
    public boolean Match(GAV other){
        if (!Objects.equals(this.name, other.name)){
            return false;
        }
        if (!Objects.equals(this.group, other.group)){
            return false;
        }
        if (this.version.trim().equals("*")||other.version.trim().equals("*")){
            return true;
        }
        return this.version.equals(other.version);
    }
}
