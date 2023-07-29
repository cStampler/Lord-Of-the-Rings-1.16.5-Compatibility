
/*
 * General purpose coremod transformer for the mod.
 * For the interested reader, these are the functions of this transformer:
 *
 * - Adding LOTR DataFixes to the DataFixerManager.
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
	IRETURN = Opcodes.IRETURN;
	ARETURN = Opcodes.ARETURN;
	RETURN = Opcodes.RETURN;
	INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
	INVOKESTATIC = Opcodes.INVOKESTATIC;
	
	return {
		"DataFixesManager#addFixers": {
			"target": {
				"type": "METHOD",
				"class": "net.minecraft.util.datafix.DataFixesManager",
				"methodName": "func_210891_a",
				"methodDesc": "(Lcom/mojang/datafixers/DataFixerBuilder;)V"
			},
			"transformer": function(methodNode) {
				var instructions = methodNode.instructions;

				var found_return = ASMAPI.findFirstInstruction(methodNode, RETURN);
				
				var newIns = new InsnList();
				newIns.add(new VarInsnNode(ALOAD, 0)); // builder
				newIns.add(new MethodInsnNode(INVOKESTATIC, "lotr/common/coremod/InjectMethods$DataFixes", "addModFixers", "(Lcom/mojang/datafixers/DataFixerBuilder;)V", false));
			
				instructions.insertBefore(found_return, newIns);
				
				return methodNode;
			}
		}
	};
}
