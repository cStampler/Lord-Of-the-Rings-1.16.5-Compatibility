package lotr.common.resources;

import java.util.*;

import net.minecraft.world.World;

public interface PostServerLoadedValidator {
	List validators = new ArrayList();

	void performPostServerLoadValidation(World var1);
}
