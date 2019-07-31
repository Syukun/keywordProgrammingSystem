package generator;

import java.util.Map;
import java.util.Set;

import astNode.Field;
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
	
	
	
	
}
