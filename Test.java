import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Dimension;


/**
 * Clase principal
 * Permite cargar un automata de pila
 * Posteriormente evalua si una gramática acepta una cadena
 * @version 1.5, 20/04/2016
 * @author Jacobo Tapia
 * @author Alan Herrera
 */

public class Test{
	public static void main(String[] args) {
		
		int option=-1;

		while(option!=3){
			try{
				option=Integer.parseInt(JOptionPane.showInputDialog(null,
					"1.Ingresa una gramatica desde archivo\n"+
					"2.Instrucciones\n"+
					"3.Salir\n",
					"Menu",JOptionPane.PLAIN_MESSAGE));
				switch(option){
					
					case 1:
						String fileName=JOptionPane.showInputDialog("Ingresa el nombre del archivo: ");
						AutomataPila automata=cargaGramatica(fileName);
						stringProcessingMenu(automata);
					break;

					case 2:
						showInstructions();
					break;

					case 3:
					break;

				}
			}catch(Exception e){
				option=-1;
			}
		}
	}


	/**
	*Lee un archivo y genera el autómata de pila
	*correspondiente a la grmática encontrada en
	*el archivo.
	*@param fileName, nombre del archivo del que se cargara la gramatica
	*@see AutomataPila, metodo setAlphabet(String[] alphabet)
	*@see AutomataPila, metodo setGrammarSymbols(String[] grammarSymbols)
	*@see AutomataPila, metodo setRootSymbol(String rootSymbol)
	*@see AutomataPila, metodo addProductionToGrammarSymbol(String symbol,String production)
	*@return AutomataPila, con la gramatica indicada en el archivo
	*/
	public static AutomataPila cargaGramatica(String fileName) throws Exception{
		
		AutomataPila automata=new AutomataPila();

		int currentLine=0;

		try{

			BufferedReader reader=new BufferedReader(
				new FileReader(fileName));

			while(reader.ready()){

				if(currentLine==0){
					/**
					*Lectura de simbolos no terminales
					*cada uno separados por comas
					*/					
					String[] data=reader.readLine().split(",");
					automata.setGrammarSymbols(data);
					currentLine++;

				}else if(currentLine==1){
					/**
					*Lectura de los simbolos del alfabeto
					*cada uno separado por comas
					*/
					String [] data=reader.readLine().split(",");
					automata.setAlphabet(data);
					currentLine++;

				}else if(currentLine==2){
					/**
					*Lectura del simbolo no terminal inicial
					*/
					String data=reader.readLine();
					automata.setRootSymbol(data);
					currentLine++;

				}else if(currentLine>2){
					/**
					*Lectura de las producciones de todos los simbolos
					*no terminales
					*/
					String[] data=reader.readLine().split("->");
					/*data[0] simbolo no terminal*/
					/*data[1] cadena producida*/
					System.out.println();
					automata.addProductionToGrammarSymbol(data[0],data[1]);
					currentLine++;
				}

			}

		}catch(Exception e){
			JOptionPane.showMessageDialog(null,
				"Ups :S, el archivo no existe");
			throw new RuntimeException("Falla en carga de archivo");
		}

		/*
		*Tras leer el archivo, se imprimira en consola los simbolos no 
		*terminales y susproducciones
		*/
		System.out.println(automata.printGrammar());
		return automata;

	}

	/**
	*Abre el menu para procesar cadenas
	*@param automata, automata de pila en el que sereán procesadas las cadenas
	*/
	public static void stringProcessingMenu(AutomataPila automata){
		int option=-1;
		while(option!=2){
			try{
				option=Integer.parseInt(JOptionPane.showInputDialog(null,
					"1.Procesa una cadena\n"+
					"2.Regresar al menu anterior\n",
					"Menu",JOptionPane.PLAIN_MESSAGE));
				switch(option){
					case 1:
					String string=JOptionPane.showInputDialog("Ingresa una cadena");
					JTextArea textArea=new JTextArea(automata.generateString(string));
					JScrollPane scrollPane=new JScrollPane(textArea);
					textArea.setLineWrap(true);
					scrollPane.setPreferredSize(new Dimension( 500, 500 ));
					JOptionPane.showMessageDialog(null, 
						scrollPane,
						"Evaluación de "+string,
		   				JOptionPane.YES_NO_OPTION);					
					break;
				}
			}catch(Exception e){
				option=-1;
			}
		}
	}

	/**
	*Muestra las instrucciones del programa
	*/
	public static void showInstructions(){
		String s="Instrucciones: \n";
		s+="\nA.El menu ofrecera tres opciones iniciales\n\t1.Cargar gramática";
		s+="\n\t2.Instrucciones\n\t3.Salir";
		s+="\n\nB.Si seleccionas cargar gramatica, se te pedira el nombre de un archivo";
		s+="\n, si es ingresado correctamente, veras los simbolos no terminales que";
		s+="\n conforman la gramática y sus producciones";
		s+="\n\nC. Si se carga el archivo podrás ingresar cadenas y ver si son aceptadas";
		s+="\n\tC.1 Si tu cadena es aceptada, verás 'La cadena es aceptada' y las movidas del automata";
		s+="\n\tC.2 Si tu cadena es rechazada, verás 'La cadena no puede ser producida' y verás los caminos rechazados";
		s+="\n\tC.3 Tu cadena puede ser rechazada si contiene simbolos fuera del alfabeto,";
		s+="\n\ten este caso, no veras movida, solo un mensaje advirtiendo que la cadena fue rechazada";
		s+="\n\nD.Si creaste tu archivo y el programa no lo reconoce, copia el contenido del archivo,";
		s+="\ncrea un archivo nuevo, pega el contenido y elimina el archivo original";
		s+="\n\nE.Encontraras en la carpeta del proyecto algunos archivos de ejemplo.";
		JTextArea textArea=new JTextArea(s);
		JScrollPane scrollPane=new JScrollPane(textArea);
		textArea.setLineWrap(true);
		scrollPane.setPreferredSize(new Dimension( 800, 345 ));
		JOptionPane.showMessageDialog(null, 
			scrollPane,
			"Instrucciones",
		   	JOptionPane.YES_NO_OPTION);		
	}

}