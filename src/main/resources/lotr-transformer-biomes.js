
/*
 * Coremod transformer to support functionality added by the mod's biomes.
 * For the interested reader, these are the functions of this transformer:
 *
 * - Patching certain methods of the base Biome class, redirecting their return values to pass through the mod's biome wrapper where appropriate.
 *   Previously achieved by overriding Biome class methods.
 *
 */

function initializeCoreMod() {

	Opcodes = Java.type("org.objectweb.asm.Opcodes");
	InsnList = Java.type("org.objectweb.asm.tree.InsnList");
	MethodNode = Java.type("org.objectweb.asm.tree.MethodNode");
	FieldNode = Java.type("org.objectweb.asm.tree.FieldNode");
	AbstractInsnNode = Java.type("org.objectweb.asm.tree.AbstractInsnNode");
	InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
	VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
	FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
	MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
	
	ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
	
	// Opcodes
	ILOAD = Opcodes.ILOAD;
	ALOAD = Opcodes.ALOAD;
	IRETURN = Opcodes.IRETURN;
	FRETURN = Opcodes.FRETURN;
	INVOKESTATIC = Opcodes.INVOKESTATIC;
	
	return {
		"Biome#doesSnowGenerate": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.world.biome.Biome",
				"methodName": "func_201850_b",
				"methodDesc": "(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z"
			},
			"transformer": function(methodNode) {
				var instructions = methodNode.instructions;
				
				var returnNodes = findAllNodes(instructions, IRETURN);
				performSanityCheck(returnNodes, 3);
				
				for (i = 0; i < returnNodes.length; i++) {
					var returnNode = returnNodes[i];
					
					var newIns = new InsnList();
					newIns.add(new VarInsnNode(ALOAD, 0)); // this
					newIns.add(new VarInsnNode(ALOAD, 1)); // reader
					newIns.add(new VarInsnNode(ALOAD, 2)); // pos
					newIns.add(new MethodInsnNode(INVOKESTATIC, "lotr/common/coremod/InjectMethods$Biomes", "doesSnowGenerate", "(ZLnet/minecraft/world/biome/Biome;Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z", false));
					instructions.insertBefore(returnNode, newIns);
				}
				
				return methodNode;
			}
		},
		"Biome#doesWaterFreeze": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.world.biome.Biome",
				"methodName": "func_201854_a",
				"methodDesc": "(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Z)Z"
			},
			"transformer": function(methodNode) {
				var instructions = methodNode.instructions;
				
				var returnNodes = findAllNodes(instructions, IRETURN);
				performSanityCheck(returnNodes, 4);
				
				for (i = 0; i < returnNodes.length; i++) {
					var returnNode = returnNodes[i];
					
					var newIns = new InsnList();
					newIns.add(new VarInsnNode(ALOAD, 0)); // this
					newIns.add(new VarInsnNode(ALOAD, 1)); // reader
					newIns.add(new VarInsnNode(ALOAD, 2)); // pos
					newIns.add(new VarInsnNode(ILOAD, 3)); // mustBeAtEdge
					newIns.add(new MethodInsnNode(INVOKESTATIC, "lotr/common/coremod/InjectMethods$Biomes", "doesWaterFreeze", "(ZLnet/minecraft/world/biome/Biome;Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Z)Z", false));
					instructions.insertBefore(returnNode, newIns);
				}
				
				return methodNode;
			}
		},
		"Biome#getTemperatureAtPosition": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.world.biome.Biome",
				"methodName": "func_242437_b",
				"methodDesc": "(Lnet/minecraft/util/math/BlockPos;)F"
			},
			"transformer": function(methodNode) {
				var instructions = methodNode.instructions;
				
				var returnNodes = findAllNodes(instructions, FRETURN);
				performSanityCheck(returnNodes, 2);
				
				for (i = 0; i < returnNodes.length; i++) {
					var returnNode = returnNodes[i];
					
					var newIns = new InsnList();
					newIns.add(new VarInsnNode(ALOAD, 0)); // this
					newIns.add(new VarInsnNode(ALOAD, 1)); // pos
					newIns.add(new MethodInsnNode(INVOKESTATIC, "lotr/common/coremod/InjectMethods$Biomes", "getTemperatureRaw", "(FLnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/BlockPos;)F", false));
					instructions.insertBefore(returnNode, newIns);
				}
				
				return methodNode;
			}
		}
	};
}

function findAllNodes(instructions, opcode) {
	var returnNodes = [];
	for (i = 0; i < instructions.size(); i++) {
		var node = instructions.get(i);
		if (node.getOpcode() === opcode) {
			returnNodes.push(node);
		}
	}
	return returnNodes;
}

function performSanityCheck(returnNodes, expectedCount) {
	if (returnNodes.length !== expectedCount) {
		throw new Error("Transformer failed sanity check! Expected to find " + expectedCount + " return nodes in method but found " + returnNodes.length);
	}
}
