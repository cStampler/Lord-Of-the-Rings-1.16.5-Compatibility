
/*
 * Coremod transformer for registering the mod's dimensions and dimension compatibility with pre-1.16 worlds.
 * For the interested reader, these are the functions of this transformer:
 *
 * - Registering the mod's dimension types and dimensions on startup.
 *   Unfortunately datapack dimensions do not seem to be workable for this at the moment given that
 *   the mod's dimension type overrides the normal dimension type class.
 * - Ensuring that pre-made 1.15 worlds loaded in 1.16 have the mod's dimension injected into the registry, which would normally not happen.
 * - Ensuring that players and maps in Middle-earth loaded from 1.15 worlds maintain their correct dimension.
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
	ALOAD = Opcodes.ALOAD;
	LLOAD = Opcodes.LLOAD;
	ASTORE = Opcodes.ASTORE;
	IRETURN = Opcodes.IRETURN;
	ARETURN = Opcodes.ARETURN;
	RETURN = Opcodes.RETURN;
	INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
	INVOKESTATIC = Opcodes.INVOKESTATIC;
	
	return {
		"DimensionType#registerTypes": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.world.DimensionType",
				"methodName": "func_236027_a_",
				"methodDesc": "(Lnet/minecraft/util/registry/DynamicRegistries$Impl;)Lnet/minecraft/util/registry/DynamicRegistries$Impl;"
			},
			"transformer": function(methodNode) {
				var instructions = methodNode.instructions;

				var found_return = ASMAPI.findFirstInstruction(methodNode, ARETURN);
				var node_loadBeforeReturn = found_return.getPrevious(); // ALOAD 1
				
				var newIns = new InsnList();
				newIns.add(new VarInsnNode(ALOAD, 1)); // local: mutableregistry
				newIns.add(new MethodInsnNode(INVOKESTATIC, "lotr/common/coremod/InjectMethods$Dimensions", "registerDimensionTypes", "(Lnet/minecraft/util/registry/MutableRegistry;)V", false));
			
				instructions.insertBefore(node_loadBeforeReturn, newIns);
				
				return methodNode;
			}
		},
		"DimensionType#getDefaultSimpleRegistry": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.world.DimensionType",
				"methodName": "func_242718_a",
				"methodDesc": "(Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;J)Lnet/minecraft/util/registry/SimpleRegistry;"
			},
			"transformer": function(methodNode) {
				var instructions = methodNode.instructions;

				var found_return = ASMAPI.findFirstInstruction(methodNode, ARETURN);
				var node_loadBeforeReturn = found_return.getPrevious(); // ALOAD 5
				
				var newIns = new InsnList();
				newIns.add(new VarInsnNode(ALOAD, 5)); // local: simpleregistry
				newIns.add(new VarInsnNode(ALOAD, 0)); // param: lookUpRegistryDimensionType
				newIns.add(new VarInsnNode(ALOAD, 1)); // param: lookUpRegistryBiome
				newIns.add(new VarInsnNode(ALOAD, 2)); // param: lookUpRegistryDimensionSettings
				newIns.add(new VarInsnNode(LLOAD, 3)); // param: seed
				newIns.add(new MethodInsnNode(INVOKESTATIC, "lotr/common/coremod/InjectMethods$Dimensions", "registerWorldDimensions", "(Lnet/minecraft/util/registry/SimpleRegistry;Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;J)V", false));
			
				instructions.insertBefore(node_loadBeforeReturn, newIns);
				
				return methodNode;
			}
		},
		"SaveFormat#getReader": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.world.storage.SaveFormat",
				"methodName": "lambda$getReader$4", // Patching a lambda... uh oh
				"methodDesc": "(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/util/datafix/codec/DatapackCodec;Lnet/minecraft/world/storage/SaveFormat$LevelSave;Ljava/io/File;Lcom/mojang/datafixers/DataFixer;)Lnet/minecraft/world/storage/ServerWorldInfo;"
			},
			"transformer": function(methodNode) {
				var instructions = methodNode.instructions;

				var found_returnInfo = ASMAPI.findFirstInstruction(methodNode, ARETURN);
				
				var newIns = new InsnList();
				newIns.add(new VarInsnNode(ALOAD, 0)); // param: nbt
				newIns.add(new VarInsnNode(ALOAD, 2)); // param: levelSave
				newIns.add(new VarInsnNode(ALOAD, 13)); // local: info
				newIns.add(new MethodInsnNode(INVOKESTATIC, "lotr/common/coremod/InjectMethods$Dimensions", "addModDimensionToOldWorlds", "(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/world/storage/SaveFormat$LevelSave;Lnet/minecraft/world/storage/ServerWorldInfo;)V", false));
			
				instructions.insertBefore(found_returnInfo, newIns);
				
				return methodNode;
			}
		},
		"DimensionType#decodeWorldKey": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.world.DimensionType",
				"methodName": "func_236025_a_",
				"methodDesc": "(Lcom/mojang/serialization/Dynamic;)Lcom/mojang/serialization/DataResult;"
			},
			"transformer": function(methodNode) {
				var instructions = methodNode.instructions;

				var found_codecParse = ASMAPI.findFirstMethodCall(methodNode, ASMAPI.MethodType.INTERFACE, "com/mojang/serialization/Codec", "parse", "(Lcom/mojang/serialization/Dynamic;)Lcom/mojang/serialization/DataResult;");
				
				// sanity check - node 2 steps before code parse should be this
				var nodeAfterFound = found_codecParse.getNext();
				if (!(nodeAfterFound.getOpcode() === ARETURN)) {
					throw new Error("Transformer broken - expected ARETURN one node after, but was " + nodeAfterFound);
				}

				var newIns = new InsnList();
				newIns.add(new VarInsnNode(ALOAD, 0)); // param: dynamic
				newIns.add(new MethodInsnNode(INVOKESTATIC, "lotr/common/coremod/InjectMethods$Dimensions", "checkDecodableModWorldKey", "(Lcom/mojang/serialization/DataResult;Lcom/mojang/serialization/Dynamic;)Lcom/mojang/serialization/DataResult;", false));
			
				instructions.insert(found_codecParse, newIns);
				
				return methodNode;
			}
		}
	};
}
