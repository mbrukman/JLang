package polyllvm.ast.PseudoLLVM.Statements;

import java.util.List;

import polyglot.ast.Ext;
import polyglot.ast.Node;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.ListUtil;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyllvm.ast.PseudoLLVM.Expressions.LLVMTypedOperand;
import polyllvm.ast.PseudoLLVM.Expressions.LLVMVariable;
import polyllvm.ast.PseudoLLVM.LLVMTypes.LLVMPointerType;
import polyllvm.ast.PseudoLLVM.LLVMTypes.LLVMTypeNode;

public class LLVMGetElementPtr_c extends LLVMInstruction_c
        implements LLVMGetElementPtr {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected LLVMTypeNode typeNode;
    protected LLVMPointerType ptrType;
    protected LLVMVariable variable;
    protected List<LLVMTypedOperand> dereferenceList;

    public LLVMGetElementPtr_c(Position pos, LLVMVariable ptrVar,
            List<LLVMTypedOperand> l, Ext e) {
        super(pos, e);
        ptrType = (LLVMPointerType) ptrVar.typeNode();
        typeNode = ptrType.dereferenceType();
        variable = ptrVar;
        dereferenceList = ListUtil.copy(l, true);
    }

    @Override
    public LLVMTypeNode retType() {
        throw new InternalCompilerError("Figure out how to get return type of GEP instruction...");
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        LLVMTypeNode tn = visitChild(typeNode, v);
        LLVMPointerType ptr = visitChild(ptrType, v);
        LLVMVariable var = visitChild(variable, v);
        List<LLVMTypedOperand> dl = visitList(dereferenceList, v);
        return reconstruct(this, tn, ptr, var, dl);
    }

    protected <N extends LLVMGetElementPtr_c> N reconstruct(N n,
            LLVMTypeNode tn, LLVMPointerType ptr, LLVMVariable var,
            List<LLVMTypedOperand> dl) {
        n = typeNode(n, tn);
        n = ptrType(n, ptr);
        n = variable(n, var);
        n = dereferenceList(n, dl);
        return n;
    }

    protected <N extends LLVMGetElementPtr_c> N typeNode(N n, LLVMTypeNode tn) {
        if (n.typeNode == tn) return n;
        n = copyIfNeeded(n);
        n.typeNode = tn;
        return n;
    }

    protected <N extends LLVMGetElementPtr_c> N ptrType(N n,
            LLVMPointerType ptr) {
        if (n.ptrType == ptr) return n;
        n = copyIfNeeded(n);
        n.ptrType = ptr;
        return n;
    }

    protected <N extends LLVMGetElementPtr_c> N variable(N n,
            LLVMVariable var) {
        if (n.variable == var) return n;
        n = copyIfNeeded(n);
        n.variable = var;
        return n;
    }

    protected <N extends LLVMGetElementPtr_c> N dereferenceList(N n,
            List<LLVMTypedOperand> dl) {
        if (n.dereferenceList == dl) return n;
        n = copyIfNeeded(n);
        n.dereferenceList = dl;
        return n;
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
        super.prettyPrint(w, pp);
        w.write("getelementptr ");
        print(typeNode, w, pp);
        w.write(", ");
        print(ptrType, w, pp);
        w.write(" ");
        print(variable, w, pp);
        for (LLVMTypedOperand t : dereferenceList) {
            w.write(", ");
            print(t, w, pp);
        }
    }

}