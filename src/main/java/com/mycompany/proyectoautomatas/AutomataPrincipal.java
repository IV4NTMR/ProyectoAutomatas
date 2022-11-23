/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectoautomatas;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iv4nt
 */
public class AutomataPrincipal {
    //Constantes ID para definir a que grupo pertenece cada token de entrada
    private static final int TOKEN_NULO = 0;
    private static final int NUMERO_ENTERO = 1;
    private static final int NUMERO_FLOTANTE = 2;
    private static final int COMENTARIO = 3;
    private static final int PARENTESIS = 4;
	private static final int LLAVE = 5;
	private static final int OPERADOR_LOGICO = 6;
	private static final int OPERADOR_ARITMETICO = 7;
	private static final int ASIGNACION = 8;



	//Contadores de tokens para cada grupo
    private int contadorEnteros = 0;
    private int contadorFlotantes = 0;
    private int contadorComentarios = 0;
    private int contadorParentesis = 0;
	private int contadorLlaves = 0;
	private int contadorLogico = 0;
	private int contadorAritmetico = 0;
	private int contadorAsignacion = 0;

    //Variables Globales
    FileReader fileReader;
    BufferedReader texto;
    String ruta;
    
    public AutomataPrincipal(String ruta) {
	this.ruta = ruta;
    }
    
    public void evaluarTexto(){
	//Leemos el archivo según la ruta ingresada
	try {
	    fileReader = new FileReader(ruta);
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(AutomataPrincipal.class.getName()).log(Level.SEVERE, null, ex);
	}
	//Guardamos dicho archivo en un Buffer
	texto = new BufferedReader(fileReader);
	
	try {
	    //Recorremos el texto caracter a caracter
	    int caracter;
	    int clasificacionDelToken;
	    do{
		caracter = texto.read(); //Leemos 
		clasificacionDelToken = estado_q0((char)caracter);
		
		//Dependiendo de que clasificación le asignó el autómata incrementamos el contador correspondiente
		switch (clasificacionDelToken){
		    case NUMERO_ENTERO: contadorEnteros++;
		    break;
		    case NUMERO_FLOTANTE: contadorFlotantes++;
		    break;
		    case COMENTARIO: contadorComentarios++;
		    break;
		    case PARENTESIS: contadorParentesis++;
		    break;
		    case LLAVE: contadorLlaves++;
			break;
			case OPERADOR_LOGICO: contadorLogico++;
			break;
			case OPERADOR_ARITMETICO: contadorAritmetico++;
			break;
			case ASIGNACION: contadorAsignacion++;
		}
	    }while(caracter != -1);
	    
	    System.out.println("Número de flotantes:" + contadorFlotantes);
	    System.out.println("Número de enteros:" + contadorEnteros);
	    System.out.println("Número de comentarios:" + contadorComentarios);
	    System.out.println("Número de paréntesis:" + contadorParentesis);
		System.out.println("Número de llaves:" + contadorLlaves);
		System.out.println("Número de operadores Logicos:" + contadorLogico);
		System.out.println("Número de operadores Aritmeticos:" + contadorAritmetico);
		System.out.println("Número de Asignacion:" + contadorAsignacion);

	} catch (IOException ex) {
	    Logger.getLogger(AutomataPrincipal.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    
    //Estado en el que se evalúa inicialmente a cada nuevo token 
    public int estado_q0(char caracter) throws IOException{
	int resultado = 0;
	if(Character.isDigit(caracter)){
	    resultado = estado_Numeros_q1((char) texto.read());
	} else if (caracter == '/')
	    resultado = estado_Comentarios_q1((char) texto.read());
	else if (caracter == '(' || caracter == ')')
	    resultado = estado_Parentesis_q1((char) texto.read());
	else if (caracter == '{' || caracter == '}')
	    resultado = estado_Llaves_q1((char) texto.read());
	else if (caracter == '+' || caracter == '-' || caracter == '*' || caracter == '/' || caracter == '%') ;
		resultado = estado_Operadores_Logicos((char) texto.read());
		return resultado;
    }

	//Cuando se detecta que el nuevo token puede ser un número, vamos a este estado, donde evaluamos si es entero, flotante o nulo
    public int estado_Numeros_q1(char caracter) throws IOException{
	if (Character.isDigit(caracter))
	    return estado_Numeros_q1((char) texto.read());
	else if (finalDeToken(caracter))
	    return NUMERO_ENTERO;
	else if (caracter == '.')
	    return estado_Numeros_q2((char) texto.read()); //De encontrar un punto en el token evaluamos ahora para un flotante
	else return TOKEN_NULO;
    }
    //Después de un punto, para que un número flotante sea valido debe haber al menos un número
    public int estado_Numeros_q2(char caracter) throws IOException{
	if (Character.isDigit(caracter)) 
	   return estado_Numeros_q3((char) texto.read()); 
	else return TOKEN_NULO;
    }
    
    //Ahora nos aseguramos de que el flotante no sea nulo conteniento solo dígitos despúes del punto
    public int estado_Numeros_q3(char caracter) throws IOException{
	if (Character.isDigit(caracter)) 
	    return estado_Numeros_q3((char) texto.read());
	else if (finalDeToken(caracter))
	    return NUMERO_FLOTANTE;
	else return TOKEN_NULO;
    }
    
    //Después del diagonal debe haber un * para considerarse un comentario
    public int estado_Comentarios_q1(char caracter) throws IOException{
	if (caracter == '*'){ //Si encontramos el * avanzamos al siguiente estado
	    return estado_Comentarios_q2((char) texto.read()); 
	} else return TOKEN_NULO;
    }
    //Este estado se cicla hasta que no recibamos otro *, no hay otra forma de salir del mismo
    public int estado_Comentarios_q2(char caracter) throws IOException{
	if (caracter == '*'){
	    return estado_Comentarios_q3((char) texto.read());
	} else if (finalDeToken(caracter)) 
	    return TOKEN_NULO;
	else return estado_Comentarios_q2((char) texto.read());
    }
    
    //Después requerimos un / único y final para cerrar el comentario, de lo contrario ciclamos entre el estado actual y el anterior
    public int estado_Comentarios_q3(char caracter) throws IOException{
	if (caracter == '/')
	    return estado_Comentarios_q4((char) texto.read());
	else if (caracter == '*')
	    return estado_Comentarios_q3((char) texto.read());
	else if (finalDeToken(caracter))
	    return TOKEN_NULO;
	else return estado_Comentarios_q2((char) texto.read());
    }
    
    //Verificamos que después del / no exista nada para validar el comentario
    public int estado_Comentarios_q4(char caracter){
	if (finalDeToken(caracter))
	    return COMENTARIO;
	else return TOKEN_NULO;
    }
    
    //Verificamos que el paréntesis este aislado
    public int estado_Parentesis_q1(char caracter){
	if (finalDeToken(caracter))
	    return PARENTESIS;
	else return TOKEN_NULO;
    }
    
    //Verificamos que la llave esté aislada
    public int estado_Llaves_q1(char caracter){
	if (finalDeToken(caracter))
	    return LLAVE;
	else return TOKEN_NULO;
    }
    
    //Esta función solo nos permite revisar si el siguiente caracter es un separador (salto de línea, espacio o tabulador)
    public boolean finalDeToken(char caracter){
	if (Character.isWhitespace(caracter) || caracter == '\t' || caracter == '\n' || (int)caracter == 65535)
	    return true;
	else return false;
    }

	/*METODO LOGICO*/
	private int estado_Operadores_Logicos(char caracter) throws IOException {
		if (caracter == '+'){}
			return TOKEN_NULO;
	}
    
    //GETTERS Y SETTERS

    public int getContadorComentarios() {
	return contadorComentarios;
    }

    public int getContadorEnteros() {
	return contadorEnteros;
    }

    public int getContadorLlaves() {
	return contadorLlaves;
    }

    public int getContadorParentesis() {
	return contadorParentesis;
    }

    public int getContadorFlotantes() {
	return contadorFlotantes;
    }

	public int getContadorLogico(){
		return contadorLogico;
	}
    
    
}
