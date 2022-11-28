/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectoautomatas;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.metal.MetalBorders;

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
	private static final int OPERADOR_RELACIONAL = 9;
	private static final int TOKEN_NULO_POR_SEPARACION = 12;
	private static final int IDENTIFICADOR = 10;
	private static final int PALABRA_RESERVADA = 11;
	private static final int DEFAULT = 13;



	//Contadores de tokens para cada grupo
    private int contadorEnteros = 0;
    private int contadorFlotantes = 0;
    private int contadorComentarios = 0;
    private int contadorParentesis = 0;
	private int contadorLlaves = 0;
	private int contadorLogico = 0;
	private int contadorAritmetico = 0;
	private int contadorAsignacion = 0;
	private int contadorRelacional = 0;
	private int contadorNulos = 0;
	private int contadorIdentificadores = 0;
	private int contadorPalabrasReservadas = 0;
	

    //Variables Globales
    FileReader fileReader;
    BufferedReader texto;
    String ruta;
    private String tokenBuffer;
    private String[] palabrasReservadas = 
    {"if", "else", "switch", "case", "default", "for", "while", "break", "int", "String", "double", "char"};
    
    public AutomataPrincipal(String ruta) {
	this.ruta = ruta;
	tokenBuffer = new String("");
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
	    caracter = texto.read(); //Leemos el primer valor
	    do{
		tokenBuffer = "";
		clasificacionDelToken = estado_q0((char)caracter);
		
		//Dependiendo de que clasificación le asignó el autómata incrementamos el contador correspondiente
		switch (clasificacionDelToken){
		    case PALABRA_RESERVADA: contadorPalabrasReservadas++;
		    break;
		    case IDENTIFICADOR: contadorIdentificadores++;
		    break;
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
		    break;
		    case OPERADOR_RELACIONAL: contadorRelacional++;
		    break;
		    case TOKEN_NULO_POR_SEPARACION: contadorNulos++; //Cuando los token se anulan por un separador (Comentarios) no se avanza
			break;
		    case TOKEN_NULO: 
			contadorNulos++;
			while (!finalDeToken((char)caracter))
				caracter = texto.read();
			break;
			
		}
		
		caracter = texto.read();//Leemos el siguiente caracter y repetimos evaluación
	    }while(caracter != -1);
	   	    
	    System.out.println("Número de flotantes:" + contadorFlotantes);
	    System.out.println("Número de enteros:" + contadorEnteros);
	    System.out.println("Número de comentarios:" + contadorComentarios);
	    System.out.println("Número de paréntesis:" + contadorParentesis);
	    System.out.println("Número de llaves:" + contadorLlaves);
	    System.out.println("Número de operadores lógicos:" + contadorLogico);
	    System.out.println("Número de operadores aritméticos:" + contadorAritmetico);
	    System.out.println("Número de asignaciones:" + contadorAsignacion);
	    System.out.println("Número de operadores relacionales:" + contadorRelacional);
	    
	    //texto.close();

	} catch (IOException ex) {
	    Logger.getLogger(AutomataPrincipal.class.getName()).log(Level.SEVERE, null, ex);
	}
	
    }
    
    //Estado en el que se evalúa inicialmente a cada nuevo token 
    public int estado_q0(char caracter) throws IOException{
	int resultado = DEFAULT;
	if(Character.isDigit(caracter))
	    resultado = estado_Numeros_q1((char) texto.read());
	else if (caracter == '/')
	    resultado = estado_Comentarios_q1((char) texto.read());
	else if (caracter == '(' || caracter == ')')
	    resultado = estado_Parentesis_q1((char) texto.read());
	else if (caracter == '{' || caracter == '}')
	    resultado = estado_Llaves_q1((char) texto.read());
	else if (caracter == '*' || caracter == '%')
	    resultado = estado_aritmetico_q1((char) texto.read());
	else if (caracter == '+' || caracter == '-')
	    resultado = estado_Suma_Resta_q1((char) texto.read());
	else if (caracter == '|')
	    resultado = estado_Logico_Or((char) texto.read());
	else if (caracter == '&')
	    resultado = estado_Logico_And((char) texto.read());
	else if (caracter == '=')
	    resultado = estado_Asignacion_q1((char) texto.read());
	else if (caracter == '<' || caracter == '>')
	    resultado = estado_OperadorRelacional_q1((char) texto.read());
	else if (caracter == '!')
	    resultado = estado_OperadorNegacion_q1((char) texto.read()); //Estado especial para verificar si es nulo, relacional o lógico
	else if (Character.isLetter(caracter)){
	    tokenBuffer = tokenBuffer.concat(String.valueOf(caracter));
	    resultado = estado_Identificador_q1((char) texto.read());
	}
	return resultado;
    }
  
    public int estado_Identificador_q1(char caracter) throws IOException{
	if (Character.isLetter(caracter)|| caracter == '_'){
	    tokenBuffer = tokenBuffer.concat(String.valueOf(caracter));
	    return estado_Identificador_q1((char) texto.read());
	}
	else if (finalDeToken(caracter)){
	    System.out.println(tokenBuffer);
	    for (int i = 0; i<palabrasReservadas.length; i++){
		if (palabrasReservadas[i].equals(tokenBuffer))
		    return PALABRA_RESERVADA;
	    }
	    return IDENTIFICADOR;
	}
	else return TOKEN_NULO;
    }
    
    public int estado_Suma_Resta_q1(char caracter) throws IOException{
	if (Character.isDigit(caracter))
	    return estado_Numeros_q1((char)texto.read());
	else if (finalDeToken(caracter)){
	    return OPERADOR_ARITMETICO;
	} else return TOKEN_NULO;
    }

	//Cuando se detecta que el nuevo token puede ser un número, vamos a este estado, donde evaluamos si es entero, flotante o nulo
    public int estado_Numeros_q1(char caracter) throws IOException{
	if (Character.isDigit(caracter))
	    return estado_Numeros_q1((char) texto.read());
	else if (caracter == '.')
	    return estado_Numeros_q2((char) texto.read()); //De encontrar un punto en el token evaluamos ahora para un flotante
	else if (finalDeToken(caracter))
	    return NUMERO_ENTERO;
	else return TOKEN_NULO;
    }
    //Después de un punto, para que un número flotante sea valido debe haber al menos un número después del punto
    public int estado_Numeros_q2(char caracter) throws IOException{
	if (Character.isDigit(caracter)) 
	   return estado_Numeros_q3((char) texto.read()); 
	else if (finalDeToken(caracter)){
	    return TOKEN_NULO_POR_SEPARACION;
	}
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
	if (caracter == '*') //Si encontramos el * avanzamos al siguiente estado
	    return estado_Comentarios_q2((char) texto.read());
	else if (finalDeToken(caracter))
	    return OPERADOR_ARITMETICO;
	else return TOKEN_NULO;
    }
    //Este estado se cicla hasta que no recibamos otro *, no hay otra forma de salir del mismo
    public int estado_Comentarios_q2(char caracter) throws IOException{
	if (caracter == '*'){
	    return estado_Comentarios_q3((char) texto.read());
	} else if (finalDeToken(caracter)) {
	    return TOKEN_NULO_POR_SEPARACION;
	}
	else return estado_Comentarios_q2((char) texto.read());
    }
    
    //Después requerimos un / único y final para cerrar el comentario, de lo contrario ciclamos entre el estado actual y el anterior
    public int estado_Comentarios_q3(char caracter) throws IOException{
	if (caracter == '/')
	    return estado_Comentarios_q4((char) texto.read());
	else if (caracter == '*')
	    return estado_Comentarios_q3((char) texto.read());
	else if (finalDeToken(caracter))
	    return TOKEN_NULO_POR_SEPARACION;
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

	/****************************************METODO ARITMETICO**********************/
	private int estado_aritmetico_q1(char caracter) {
		if (finalDeToken(caracter))
			return OPERADOR_ARITMETICO;
		return TOKEN_NULO;
	}

	/***************************************METODO LOGICO***********************************************/
	
	private int estado_Logico_And(char caracter) throws IOException{
	    if (caracter == '&')
		return estado_Logico_Final((char) texto.read());
	    else if (finalDeToken(caracter))
		return TOKEN_NULO_POR_SEPARACION;
	    else return TOKEN_NULO;
	}
	
	private int estado_Logico_Or(char caracter) throws IOException{
	    if (caracter == '|')
		return estado_Logico_Final((char) texto.read());
	    else if (finalDeToken(caracter))
		return TOKEN_NULO_POR_SEPARACION;
	    else return TOKEN_NULO;
	}
	
	private int estado_Logico_Final(char caracter) throws IOException{
	    if (finalDeToken(caracter))
		return OPERADOR_LOGICO;
	    else return TOKEN_NULO;
	}
	
	/************************************Asignacion*************************************************************/
	private int estado_Asignacion_q1(char caracter) throws IOException{
	    if (finalDeToken(caracter))
		return ASIGNACION;
	    else if (caracter == '=')
		return estado_OperadorRelacional_q2((char) texto.read());
	    else return TOKEN_NULO;
	}
	
	/************************************OperadoresRelacionales************************************************/
	
	private int estado_OperadorRelacional_q1(char caracter){
	    if (finalDeToken(caracter))
		return OPERADOR_RELACIONAL;
	    else if (caracter == '='){
		return OPERADOR_RELACIONAL;
	    }
	    else return TOKEN_NULO;
	}
	
	private int estado_OperadorRelacional_q2(char caracter){
	    if (finalDeToken(caracter))
		return OPERADOR_RELACIONAL;
	    else return TOKEN_NULO;
	}
	
	/************************************OperadoresAmbiguos**************************************************/
	//Los operadores ambiguos son aquellos que estando al inicio del token, su posible clasificación es ambigua en un inicio
	private int estado_OperadorNegacion_q1(char caracter){
	    if (finalDeToken(caracter))
		return OPERADOR_LOGICO;
	    else if (caracter == '=')
		return OPERADOR_RELACIONAL;
	    else return TOKEN_NULO;
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
/********************************Mis gets & sets*******************************************************************/
	public int getContadorLogico(){
		return contadorLogico;
	}
	public int getContadorAritmetico(){
		return contadorAritmetico;
	}
	public int getContadorAsignacion(){
		return contadorAsignacion;
	}

    public int getContadorRelacional() {
	return contadorRelacional;
    }

    public int getContadorNulos() {
	return contadorNulos;
    }

    public int getContadorIdentificadores() {
	return contadorIdentificadores;
    }

    public int getContadorPalabrasReservadas() {
	return contadorPalabrasReservadas;
    }
    
    
    
    
	
    
    
}
