package cc.catman.plugin.resources;

import org.springframework.core.io.Resource;

public abstract class FilterResourceVisitor implements ResourceVisitor{
   protected abstract boolean filter(Resource resource);
   protected abstract boolean doVisitor(Resource resource);
   @Override
   public boolean visitor(Resource resource) {
      return filter(resource)&&doVisitor(resource);
   }
}
