package org.netbeans.modules.php.opencart.editor.visitors;

import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.NavUtils;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayAccess;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayDimension;
import org.netbeans.modules.php.editor.parser.astnodes.Assignment;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.ExpressionStatement;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.VariableBase;
import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.modules.php.opencart.editor.PhpUtils;
import org.openide.filesystems.FileObject;

public class OpenCartControllerVisitor extends OpenCartFieldsVisitor {

    private final Set<PhpVariable> fields = new HashSet<>();
    private static final String DATA_VAR = "$data";
    private String methodName = "";

    public OpenCartControllerVisitor(FileObject targetFile) {
        super(targetFile);
    }

    @Override
    public void visit(ExpressionStatement node) {
        super.visit(node);
        String fileName = targetFile.getName();
        String searchMethodName = "index";

        if (fileName.endsWith("_from")){
            searchMethodName = "getForm";
        } else if (fileName.endsWith("_list")){
            searchMethodName = "getList";
        }else if (fileName.endsWith("_info")){
            searchMethodName = "info";
        }

        if (methodName == null || !methodName.equals(searchMethodName)) {
            return;
        }

        Expression expression = node.getExpression();
        if (expression instanceof Assignment) {
            Assignment assignment = (Assignment) expression;
            Assignment.Type operator = assignment.getOperator();
            if (operator != Assignment.Type.EQUAL) {
                return;
            }

            VariableBase leftHandSide = assignment.getLeftHandSide();
            if (leftHandSide instanceof ArrayAccess) {
                ArrayAccess f = (ArrayAccess) leftHandSide;
                if (DATA_VAR.equals(CodeUtils.extractVariableName((Variable) f.getName()))){
                    ArrayDimension dimension = f.getDimension();
                    Expression e = dimension.getIndex();
                    if (e instanceof Scalar){
                        Scalar s = (Scalar) e;
                        if (s.getScalarType() == Scalar.Type.STRING) {
                            String name = NavUtils.dequote(s.getStringValue());
                            fields.add(new PhpVariable(OpenCart.DOLLAR +name, PhpUtils.getPhpClass("string", false), targetFile, 0));
                        }
                    }
                }
            }
        }
    }

    public Set<PhpVariable> getPhpVariables() {
        Set<PhpVariable> phpVariables = new HashSet<>();
        synchronized (fields) {
            phpVariables.addAll(fields);
        }
        return phpVariables;
    }

    @Override
    public void visit(MethodDeclaration node) {
        methodName = CodeUtils.extractMethodName(node);
        super.visit(node);
    }

}
