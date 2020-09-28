#include <avr/io.h>
#include <util/delay.h>
#include <stdio.h>
#include <string.h>
#include <avr/interrupt.h>
#include "serial.h"
//#include "lcd.h"

#define FOSC 7372800           // Clock frequency
#define BAUD 9600               // Baud rate used
#define MYUBRR FOSC/16/BAUD-1   // Value for UBRR0 register
#define serial_str_size 16      // size of string to send 

extern volatile char msg_recv;
extern volatile char received_str[serial_str_size];
extern volatile unsigned char received_index;// = 0;
extern volatile char received_flag;// = 0;

//unsigned short ubrr_value
void serial_init()
{

    // Set up USART0 registers
     UBRR0 = MYUBRR;                       // Set baud rate
     UCSR0B |= (1 << TXEN0)|(1 << RXEN0);  // Enable RX and TX
     UCSR0C = (3 << UCSZ00);               // Async, no parity, 1 stop bit, 8 data bits
     UCSR0B |= (1 << RXCIE0);              // Enable receive interrupts
    // initialize global variables
    received_index = 0;
    received_flag = 0; 
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

char rx_char()
{
// Wait for receive complete flag to go high
    while ( !(UCSR0A & (1 << RXC0)) ) {}
    return UDR0;
}

ISR(USART_RX_vect)
{
    // lcd_moveto(1,0);
    // lcd_stringout("RX interrupt");
    // Handle received character
    char received_char = UDR0;
    // lcd_moveto(0,8+index);
    // lcd_writedata(received_char);

    received_str[received_index] = received_char;
    received_index++;
    
    if (received_index == 2)
    {
       received_flag = 1;
       received_index = 0;
    }
    // PORTD |= (1 << PD3);
}
