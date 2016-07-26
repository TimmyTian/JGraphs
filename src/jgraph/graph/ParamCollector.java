
package jgraph.graph;

import java.util.Stack;

/**
 * The ParamCollector is used to collect parameters from a String in the format
 * ("string","string" , "string" , ... , "string")
 * @author Richard DeSilvey
 */
public class ParamCollector {
    /**
     * Parses a string in the form "(ABC,ABC,...,ABC)" where ABC is some string
     * and returns a stack of each parameter value. Spaces are allowed.
     * @param str The string being parsed
     * @return The stack of parameter values.
     */
    public static Stack<String> getParams(String str){
        Stack<String> params = new Stack();
        StringBuilder paramBuilder = new StringBuilder();
        
        for (int i = 1;; i++){
            
            if (i >= str.length()){
                throw new IllegalArgumentException("End of Param not Found ')'");
            }
            
            if (str.charAt(i) == ',' || str.charAt(i) == ' '){
                if (paramBuilder.length() > 0){
                    params.push(paramBuilder.toString());
                    paramBuilder.delete(0, paramBuilder.length());
                }else{
                    throw new IllegalArgumentException("No Param found");
                }
                continue;
            }else if (str.charAt(i) == ')'){
                if (paramBuilder.length() > 0){
                    params.push(paramBuilder.toString());
                    paramBuilder.delete(0, paramBuilder.length());
                }
                break;
            }
            paramBuilder.append(str.charAt(i));
        }
        return params;
    }

    private ParamCollector() {
    }
}
