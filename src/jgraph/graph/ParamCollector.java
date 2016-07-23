
package jgraph.graph;

import java.util.Stack;

/**
 * 
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
        
        String p = "";
        
        for (int i = 1; i < str.length(); i++){
            if (str.charAt(i) == ',' || str.charAt(i) == ')' || str.charAt(i) == ' '){
                if (p.length() > 0){
                    params.push(p);
                    p = "";
                }
                continue;
            }
            p += str.charAt(i);
        }
        return params;
    }

    private ParamCollector() {
    }
}
