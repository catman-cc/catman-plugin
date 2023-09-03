package cc.catman.plugin.resources;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceBrowserResult {
    private boolean handled;
    private boolean continueVisitor;
    public static ResourceBrowserResult of(boolean handled,boolean continueVisitor){
        return ResourceBrowserResult.builder().handled(handled).continueVisitor(continueVisitor).build();
    }
}
