import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;


/**
 * Una clase que representa un automata de pila.
 * @version 1.6, 19/04/2016
 * @author Jacobo Tapia
 * @author Alan Herrera
 */

public class AutomataPila{

	private ArrayList<String> alphabet;
	private HashMap<String,ArrayList<String>> grammar;
	private String rootSymbol;
	private ArrayList<AuxiliarStack> auxiliarStacks;

	/**
	*Metodo constructor
	*/
	public AutomataPila(){
		this.grammar=new HashMap<String,ArrayList<String>>();
		this.auxiliarStacks=new ArrayList<AuxiliarStack>();
		this.alphabet=new ArrayList<String>();
	}

	/**
	*Recibe todos los simbolos que conforman el alfabeto de la gramatica
	*@param alphabet, cada uno de los simbolos del alfabeto para esta gramatica
	*/
	public void setAlphabet(String[] alphabet){
		for (String letter : alphabet) {
			if(!this.alphabet.contains(letter)){
				this.alphabet.add(letter);
			}
		}
	}


	/** 
     * Recibe todos los simbolos no terminales que conforman la gramatica
     * @param grammarSymbols, cada uno de los simbolos no terminales de la gramatica.
     */
	public void setGrammarSymbols(String[] grammarSymbols){
		for (String symbol:grammarSymbols) {
			this.grammar.put(symbol,new ArrayList<String>());
		}
	}


	/** 
     * Aniade una producción a un simbolo no terminal de una gramatica.
     * @param symbol, simbolo no terminal al que se quiere aniadir una produccion.
     * @param production, cadena que produce dicho simbolo no terminal
     */
	public void addProductionToGrammarSymbol(String symbol,String production){
		if(production.equals("lmd")){
			production="ª";
		}
		if(this.grammar.containsKey(symbol)){
			this.grammar.get(symbol).add(production);
		}
	}

	/**
	*Define el simbolo raiz de los simbolos no terminales
	*@param rootSymbol, simbolo no terminal inicial
	*/
	public void setRootSymbol(String rootSymbol){
		this.rootSymbol=rootSymbol;
	}

	/**
	*En caso de que una cadena pueda ser producida por la gramatica
	*regresara las movidas necesarias
	*En caso contrario, regresara que no es posible producir la cadena
	*@param string, cadena que se desea saber si puede ser producida
	*@see stringInAlphabet(String string)
	*@see initializeStack(String string)
	*@see AuxiliarStack, metodo getStack()
	*@see AuxiliarStack, metodo getCurrentString()
	*@see AuxiliarStack, metodo setCurrentString(String currentString)
	*@see AuxiliarStack, metodo setAcepted(boolean acepted)
	*@see AuxiliarStack, metodo getAcepted()
	*@see AuxiliarStack, metodo setDeleteMe(boolean deleteMe)
	*@see AuxiliarStack, metodo getDeleteMe()
	*@see AuxiliarStack, metodo pushProductionToStack(String production)
	*@see AuxiliarStack, metodo process()
	*@return cadena que indica las movidas del automata o la frase "La cadena no puede ser producida"
	*/
	public String generateString(String string) throws CloneNotSupportedException{

		String s="";
		String rejectedPaths="";
		/*
		*Verificamos que la cadena a evaluar
		*contenga simbolos dentro del alfabeto
		*de esta forma, no tenemos que procesar usando el automata de pila
		*evitando evaluar casos como ababababababc donde el simbolo c 
		*esta fuera del alfabeto (en este caso) y se encuentra al final de la cadena
		*/
		if(stringInAlphabet(string)==false){
			s="La cadena no puede ser producida, pues tiene simbolos fuera del alfabeto";
		}else{
			/*
			*Se inicializan los stacks de acuerdo al número de producciones
			*que pueda tener el simbolo no terminal 'raiz'
			*/
			initializeStacks(string);
			boolean acepted=false;
			while(acepted==false){
				/*
				*Si tenemos la lista de stacks vacia significa que
				*no hubo un camino para producir la cadena
				*/
				if(auxiliarStacks.size()==0){
					s="La cadena no puede ser producida\n\nLista de caminos rechazados:\n";
					s+=rejectedPaths;
					break;
				}
				for(int i=0;i<this.auxiliarStacks.size();i++){
					AuxiliarStack aux=auxiliarStacks.get(i);
					if(aux.getAcepted()){
						/*
						*Verificar si tras haber sido procesada
						*una de las posibilidades llego a un estado aceptor
						*/
						acepted=true;
						s="Cadena Aceptada\n";
						s+=aux;
						break;
					}

					char top=aux.getStack().peek();

					if(aux.getDeleteMe()==false && Character.isUpperCase(top) && top!='Z'){
						/*
						*Se verifica que el stack no haya sido rechazado,
						*se tenga un simbolo no terminal en la pila 
						*y que el simbolo no terminal sea diferente de Z
						*/
						ArrayList<String> productions=grammar.get(String.valueOf(top));
						ArrayList<AuxiliarStack> paths=new ArrayList<AuxiliarStack>();
						for(int k=0;k<productions.size();k++){
							/*
							*Se genera un clone del AuxiliarStack
							*de esta forma se evitan referencias
							*iguales en memoria; permitiendo que 
							*al alterar el clon, el original no se modifique.
							*/
							AuxiliarStack aux2=(AuxiliarStack)aux.clone();
							aux2.setCurrentString(aux.getCurrentString());
							aux2.setMovements(aux.getMovements());
							aux2.setAcepted(aux.getAcepted());
							aux2.setDeleteMe(aux.getDeleteMe());	
							aux2.pushProductionToStack(productions.get(k));	
							auxiliarStacks.add(aux2);
						}
						/*
						*Tras haber generado los nuevos caminos
						*Debemos marcar el stack del que se genero
						*para que sea borrado y no volvamos a procesarlo
						*/
						auxiliarStacks.get(i).setDeleteMe(true);
					}else if(aux.getDeleteMe()==false && Character.isLowerCase(top)){
						/*
						*Aqui se procesa en dado caso que el tope de la pila
						*sea un simbolo terminal
						*/
						aux.process();
						if(aux.getDeleteMe()){
							/*
							*Si tras procesar, se llega a un estado error
							*Se aniade a la lista de caminos rechazados.
							*/
							rejectedPaths+=aux+"\n\n";
						}
					}
				}

				/* Removemos aquellos stack marcados como deleteMe */
				for(int i=0;i<auxiliarStacks.size();i++){
					if(auxiliarStacks.get(i).getDeleteMe()==true){
						auxiliarStacks.remove(i);
					}
				}
			}
		}
		return s;
	}



	/**
	*Reinicia la lista de stacks con las posibles
	*producciones del simbolo raiz
	*/
	private void initializeStacks(String string){
		this.auxiliarStacks=new ArrayList<AuxiliarStack>();
		ArrayList<String> productions=grammar.get(this.rootSymbol);
		for(int i=0;i<productions.size();i++){
			AuxiliarStack aux=new AuxiliarStack(string,this.rootSymbol);
			aux.pushProductionToStack(productions.get(i));
			this.auxiliarStacks.add(aux);
		}
	}

	/**
	*Determina si una cadena contiene unicamente los simbolos del alfabeto definido
	*@param string, cadena que se evaluaran cada uno de sus caracteres
	*/
	private boolean stringInAlphabet(String string){
		boolean stringInAlphabet=true;
		char[] letters=string.toCharArray();
		for (char letter : letters) {
			if(!this.alphabet.contains(String.valueOf(letter))){
				stringInAlphabet=false;
				break;
			}
		}
		return stringInAlphabet;
	}

	/**
	*@return Regresa una cadena con todos los simbolos no terminales y su pruducciones
	*/
	public String printGrammar(){
		String s="";
		Set<String> keys = grammar.keySet();
		for (String key : keys) {
			s+=key+"-> ";
			ArrayList<String> productions=grammar.get(key);
			for(int i=0;i<productions.size();i++){
				if(i!=productions.size()-1){
					s+=productions.get(i)+" | ";
				}else{
					s+=productions.get(i);
				}
			}
			s+="\n";
		}
		return s;		
	}

}