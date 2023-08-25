package cc.catman.plugin.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MethodDescribe {
    private String name;
    private TypeDescriptor resultType;
    private List<TypeDescriptor> parametersType;

    private Method method;

    public MethodDescribe(Method method) {
        this.method = method;
        ReflectionUtils.makeAccessible(method);
        this.name = method.getName();
        this.resultType = TypeDescriptor.valueOf(method.getReturnType());
        this.parametersType = Stream.of(method.getParameterTypes())
            .map(TypeDescriptor::valueOf).collect(Collectors.toList());
    }

    public int parameterCount() {
        return Optional.ofNullable(parametersType).map(List::size).orElse(0);
    }

    public boolean isAssignableTo(Method m) {
        return isAssignableTo(new MethodDescribe(m));
    }

    public boolean isAssignableTo(MethodDescribe md) {
        // 名称必须一致
        if (!this.name.equals(md.getName())) {
            return false;
        }
        // 形参数量必须一致
        if (this.parameterCount() != md.parameterCount()) {
            return false;
        }
        // 当前返回值可以分配给目标返回值
        if (!this.resultType.isAssignableTo(md.resultType)) {
            return false;
        }
        // 所有的目标形参必须可顺序分配给当前形参
        for (int i = 0; i < md.getParametersType().size(); i++) {
            TypeDescriptor mp = md.getParametersType().get(i);
            TypeDescriptor cp = this.getParametersType().get(i);
            if (!mp.isAssignableTo(cp)) {
                return false;
            }
        }
        return true;

    }

}
