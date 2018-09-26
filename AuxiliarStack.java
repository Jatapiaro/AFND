import java.util.Stack;
import java.util.ArrayList;

/**
 * Clase para manejar el stack y la cadena procesada
 * Debido a que un simbolo no terminal puede tener diversas producciones
 * la cadena que se pueda procesar tras aplicar cierta produccion no es igual 
 * en todos los stacks.
 * En esta clase se engloba el stack asi como la cadena que se tiene hasta el momento
 * ademas de los movimientos ya hechos.
 * @version 2.0, 18/04/2016
 * @author Jacobo Tapia
 * @author Alan Herrera
 */

public class AuxiliarStack implements Cloneable{

	private Stack<Character> stack;
	private String currentString,movements;
	private boolean deleteMe,acepted;

	/**
	*Metodo Constructor
	*@param currentString, la cadena que debera ser procesada
	*@param root, el simbolo no terminal 'raiz'
	*/
	public AuxiliarStack(String currentString,String root){
		this.stack=new Stack<Character>();
		this.stack.push('Z');
		this.currentString=currentString;
		this.movements="(q0,lmd,Z)=>(q0,"+currentString+","+root+"Z)";
		acepted=deleteMe=false;
	}

	
	/**
	*Metodo de la interfaz Cloneable
	*Permite clonar un objeto de esta clase
	*de tal forma que no haya referencias en memoria
	*a los atributos del objeto, evitando asi que al alterar
	*un clon se altere el original
	*/
	@Override
	@SuppressWarnings("unchecked")
    protected Object clone() throws CloneNotSupportedException {
        AuxiliarStack clone=(AuxiliarStack)super.clone();
        clone.setStack((Stack<Character>)clone.getStack().clone());
        return clone;
    }

    /**
    *Verifica que en el tope de la pila haya un simbolo no terminal
    *@return true si hay un simbolo no terminal en el tope de la pila y y el topo es distinto a 'Z'
    */
	public boolean terminalAtTop(){
		return (this.stack.peek()!='Z' && Character.isUpperCase(this.stack.peek()))? true:false;
	}

	/**
	*Remplaza del tope de la pila una simbolo no terminal, por una de sus producciones
	*@param production, Produccion que sera insertada en la pila
	*/
	public void pushProductionToStack(String production){


		if(terminalAtTop()){
			this.stack.pop();
		}


		char[] letters=production.toCharArray();
		for(int i=letters.length-1;i>=0;i--){
			stack.push(letters[i]);
		}
		this.movements+="=>(q1,"+currentString+","+stringRepresentationOfStack()+")";

	}

	/**
	*Realiza el proceso del stack, mientras en el rope de la pila
	*haya un simbolo no terminal.
	*/
	public void process(){

		while(Character.isLowerCase(stack.peek()) && currentString.length()!=0){
			if(stack.peek()==currentString.charAt(0)){
				/*
				*Si coincide el principio de la cadena
				*con el tope de la pila, se elimina el primer elemento de la cadena
				*y se hace pop a la pila
				*/
				currentString=currentString.substring(1);
				this.stack.pop();
				this.movements+="=>(q1,"+currentString+","+stringRepresentationOfStack()+")";
			}else if(stack.peek()=='ª' && stack.size()>2){
				/*
				*En caso de tener lambda:
				*Se verifica que la pila tenga un tamaño mayor a dos,
				*es decir que no sea 'lmdZ', para unicamente hacer pop
				*/
				this.stack.pop();
				this.movements+="=>(q1,"+currentString+","+stringRepresentationOfStack()+")";
			}else{
				/*
				*Si no se cumple ningun caso anterior 
				*o es diferente el inicio de la cadena con el tope de la pila
				*se marca el stack para ser eliminado
				*/
				this.deleteMe=true;
				break;
			}
		}

		if(currentString.length()==0 && stack.peek()=='Z'){
			/*
			*Caso 1, la cadena ha sido leida
			*y la pila esta vacia
			*/
			acepted=true;
			this.movements+="=>(q2,lmd,Z)";
		}else if(currentString.length()==0 && stack.peek()!='Z' && stack.size()>2){
			/*
			*Caso 2, la cadena esta vacia pero la pila no
			*IMPORTANTE: En el caso de que la pila quede un solo simbolo terminal
			*dejando su tamaño en dos, se debe dejar dar una iteracion mas,
			*en caso de que el simbollo no terminal pueda producir lambda
			*/
			deleteMe=true;
		}else if(currentString.length()>0 && stack.peek()=='Z'){
			/*
			*Caso 3, la cadena no esta vacia y la pila esta vacia
			*/
			deleteMe=true;
		}else if(currentString.length()==0 && stringRepresentationOfStack().equals("ªZ")){
			/*
			*Caso 4, estado acepto extra
			*Cuando nuestra cadena esta vacia y tenemos ª que signica lambda en el tope de la pila
			*Indicando que esta cadena puede ser aceptada
			*/
			acepted=true;
			this.movements+="=>(q2,lmd,Z)";
		}



	}

	/**
	*El metodo toString de la clase Stack, regresa [1,2,3], donde 3 es el tope de la pila
	*para ponerlo en los movimientos necesitamos que este de la forma 321Z
	*permitiendo poner (qn,someString,321Z)
	*@return s, cadena con la representacion de la pila necesaria para los movimientos
	*/
	private String stringRepresentationOfStack(){
		char[] stackChars=this.stack.toString().replace("[","").replace("]","").replace(",","").replace(" ","").toCharArray();
		String s="";
		for(int i=stackChars.length-1;i>=0;i--){
			s+=stackChars[i];
		}
		return s;
	}

	/**
	*Define para el atributo Stack
	*@param stack, stack que se quiere definir en la clase
	*/
	public void setStack(Stack<Character> stack){
		this.stack=stack;
	}

	/**
	*@return stack,regresa el stack de esta clase
	*/
	public Stack<Character> getStack(){
		return this.stack;
	}

	/**
	*Define la cadena que sera procesada
	*@param currentString, cadena que sera procesada
	*/
	public void setCurrentString(String currentString){
		this.currentString=currentString;
	}

	/**
	*@return currentString, El valor actual de la cadena procesada
	*/
	public String getCurrentString(){
		return this.currentString;
	}

	/**
	*Define un string con los movimientos que ha hecho un AuxiliarStack
	*para procesar una cadena
	*@param movements, Movimientos realizados por un AuziliarStack
	*/
	public void setMovements(String movements){
		this.movements=movements;
	}

	/**
	*@return movements, string con los movimientos que ha hecho un AuxiliarStack para procesar una cadena
	*/
	public String getMovements(){
		return this.movements;
	}

	/**
	*@return acepted, valor que indica si el camino de este stack llego a un estado aceptor
	*/
	public boolean getAcepted(){
		return this.acepted;
	}

	/**
	*Define el valor de acepted(Si el camino que ha tomado este stack lleva a un estado aceptor)
	*@param acepted
	*/
	public void setAcepted(boolean acepted){
		this.acepted=acepted;
	}

	/**
	*Define deleteMe
	*@param deleteMe, que indica si este SStack debe ser eliminado de los posibles caminos
	*/
	public void setDeleteMe(boolean deleteMe){
		this.deleteMe=deleteMe;
	}

	/**
	*@return deleteMe, indicando si este AuxiliarStack debe ser borrado de los posibles caminos 
	*/
	public boolean getDeleteMe(){
		return this.deleteMe;
	}

	/**
	*Regresa como valor las movidas realizadas por la clase AuxiliarStack
	*@return movements, se le aniaden algunos replacements para que se muestre el resultado correcto
	*/
	public String toString(){
		return movements.replace("(q1,,Z)","(q1,lmd,Z)").replace("ª","lmd");
	}
}