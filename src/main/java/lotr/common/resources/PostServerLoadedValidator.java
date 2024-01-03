package lotr.common.resources;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;

public interface PostServerLoadedValidator {
	List<PostServerLoadedValidator> validators = new ArrayList<>();

	void performPostServerLoadValidation(World var1);
}
