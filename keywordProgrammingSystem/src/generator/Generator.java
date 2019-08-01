package generator;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import astNode.Code;
import astNode.Expression;
import astNode.Field;
import astNode.FieldAccess;
import astNode.LocalVariable;
import astNode.Type;

/**
* @author Archer Shu
* @date 2019/07/31
*/
public abstract class Generator {
	Map<String, Type> typeHierachyDictionary;
	Set<String> allPossibleTypes;
	
	Map<String, Set<LocalVariable>> localVariablesRet;
	Map<String, Set<LocalVariable>> localVariablesRec;
	
	Map<String, Set<Field>> fieldsRet;
	Map<String, Set<Field>> fieldsRec;
	
	Map<String, Vector<? extends Code>> codesInExactDepth;
	Map<String, Vector<? extends Code>> codesUnderDepth;
	
	
	
	public void initCodeTable(){
		codesInExactDepth.put("FieldAccess", new Vector<FieldAccess>());
		codesInExactDepth.put("Expression", new Vector<Expression>());
	}
	
	
}
