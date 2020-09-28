#include <avr/io.h>
#include <util/delay.h>
#include <stdio.h>
#include <string.h>
#include <avr/interrupt.h>

#include "lcd.h"

// Serial communications functions and variables
void serial_init(unsigned short);
void serial_stringout(char *, unsigned char);
void serial_txchar(char);
char rx_char(void);

#define FOSC 7372800           // Clock frequency
#define BAUD 9600               // Baud rate used
#define MYUBRR FOSC/16/BAUD-1   // Value for UBRR0 register
#define serial_str_size 2      // size of string to send 

volatile char msg_recv;
volatile char received_str[serial_str_size];
volatile unsigned char index = 0;
volatile char received_flag = 0;

int main(void)
{
	sei();
	lcd_init();
    serial_init(MYUBRR);
	//char msg_send = 'a';
	//serial_txchar(msg_send);
	char *serial_test = "abcdefg";
	//serial_stringout(serial_test, 7);
	//lcd_moveto(0,0);
	//lcd_stringout(serial_test);
	char recv;
	
	while (1)
	{
	   serial_stringout('1',1);
	   /*
	   if (received_flag)
	   {
	      lcd_moveto(0,0);
	      lcd_stringout(received_str);
	      received_flag = 0;
	      
	      //serial_stringout('1',1);
	   }
	   */
	   /*
	   recv = rx_char();
	   lcd_moveto(0,0);
	   lcd_writedata(recv);
	   */
	}
	
	
	/*
    lcd_moveto(0,0);
    lcd_writedata(msg_send);
	
    lcd_moveto(1,0);
    lcd_writedata(msg_recv);
    */
   return 0;
}



/* ----------------------------------------------------------------------- */

void serial_init(unsigned short ubrr_value)
{

    // Set up USART0 registers
     UBRR0 = MYUBRR;                       // Set baud rate
     UCSR0B |= (1 << TXEN0)|(1 << RXEN0);  // Enable RX and TX
     UCSR0C = (3 << UCSZ00);               // Async, no parity, 1 stop bit, 8 data bits
     UCSR0B |= (1 << RXCIE0);              // Enable receive interrupts
    // Enable tri-state not used here
}

void serial_txchar(char ch)
{
    while ((UCSR0A & (1<<UDRE0)) == 0);
    UDR0 = ch;
}

void serial_stringout(char *s, unsigned char size)
{
    // Call serial_txchar in loop to send a string
    int i = 0;
    while (i < size)
    {
       serial_txchar(s[i]);
       //lcd_moveto(0,8+i);
       //lcd_writedata(s[i]);
       _delay_ms(100);
       i++;
    }
    
}

ISR(USART_RX_vect)
{
    lcd_moveto(1,0);
    lcd_stringout("RX interrupt");
    // Handle received character
    char received_char = UDR0;
    lcd_moveto(0,8+index);
    lcd_writedata(received_char);

    received_str[index] = received_char;
    index++;
    
    if (index == 2)
    {
       received_flag = 1;
       index = 0;
    }

}

char rx_char()
{
// Wait for receive complete flag to go high
    while ( !(UCSR0A & (1 << RXC0)) ) {}
    return UDR0;
}













