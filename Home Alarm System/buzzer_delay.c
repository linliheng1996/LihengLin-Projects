#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
// #include "lcd.h"
#include "buzzer_delay.h"

//void init_timer1(int);
// void variable_delay_us(int);
// void play_note(unsigned short);

// int main(void)
// {
//   //lcd_init();
//   DDRB |= (1 << PB0);
//   //sei();
//  //init_timer1(62);
//  //while(1)
//  //{
//     play_note(4000);
//  //}
  

//   return 0;
// }

/*
void init_timer1(int m){                       
      TCCR1B |= (1 << WGM12);             // set to CTC mode
      TIMSK1 |= (1 << OCIE1A);            // enable timer interrupt, local interrupt bit = 1
      OCR1A = m;                          // load the max
      TCCR1B |= (1 << CS11)|(1 << CS10);  // pre-scalar = 64
      DDRB |= (1 << PB0);                 // set PC4 to be output, PC4 = 0 initially
}

//output compare a match interrupt, 16-bit timer interrupt service routine
ISR(TIMER1_COMPA_vect){
     // interrupt every half of period
     // invert the output port bit each time invoked
     PORTB ^= (1 << PB0);
     lcd_moveto(0, 0);
     lcd_writedata('T');
}
*/



//  Play a tone at the frequency specified for one second

void play_note(unsigned short freq)
{
    unsigned long period;

    period = 1000000 / freq;      // Period of note in microseconds
    int iter=0;
    while (freq--) {
	// Make PB0 high
       PORTD |= (1 << PD7);
	// Use variable_delay_us to delay for half the period
       variable_delay_us(period/2);
	// Make PB4 low
       PORTD &= ~(1 << PD7);
	// Delay for half the period again
       variable_delay_us(period/2);
       //iter++;
      // lcd_moveto(0, 0);
      // lcd_writedata('0'+iter);
    }
}


//    variable_delay_us - Delay a variable number of microseconds

void variable_delay_us(int delay)
{
    int i = (delay + 5) / 10;

    while (i--)
        _delay_us(10);
}



