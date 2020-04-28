#define CHANNEL1 0
#define CHANNEL2 1
#define CHANNEL3 2
#define CHANNEL4 3

#define YAW      0
#define PITCH    1
#define ROLL     2
#define THROTTLE 3
#include <Servo.h>

Servo motA, motB, motC, motD;
char data;
volatile unsigned long current_time;
volatile unsigned long timer[4];


volatile byte previous_state[4];

volatile unsigned int pulse_duration[4] = {1500, 1500, 1000, 1500};

int mode_mapping[4];
int max_[4]={2000,2000,2000,2000};
int min_[4]={1000,1000,1000,1000};

void setup()
{
    Serial.begin(9600);


    motA.attach(4, 1000, 2000); 
    motB.attach(5, 1000, 2000);
    motC.attach(6, 1000, 2000);
    motD.attach(7, 1000, 2000);

    displayInstructions();
    
    // Customize mapping of controls: set here which command is on wich channel.
    mode_mapping[YAW]      = CHANNEL4;
    mode_mapping[PITCH]    = CHANNEL2;
    mode_mapping[ROLL]     = CHANNEL1;
    mode_mapping[THROTTLE] = CHANNEL3;

    PCICR  |= (1 << PCIE0);  //Set PCIE0 to enable PCMSK0 scan.
    PCMSK0 |= (1 << PCINT0); //Set PCINT0 (digital input 8) to trigger an interrupt on state change.
    PCMSK0 |= (1 << PCINT1); //Set PCINT1 (digital input 9)to trigger an interrupt on state change.
    PCMSK0 |= (1 << PCINT2); //Set PCINT2 (digital input 10)to trigger an interrupt on state change.
    PCMSK0 |= (1 << PCINT3); //Set PCINT3 (digital input 11)to trigger an interrupt on state change.
}


 void loop() {
  //dumpChannels();
    if (Serial.available()) {
        data = Serial.read();

        switch (data) {
            // 0
            case 48 : Serial.println("Sending 0 throttle");
                      motA.write(min_[0]);
                      motB.write(min_[1]);
                      motC.write(min_[2]);
                      motD.write(min_[3]);
            break;

            // 1
            case 49 : Serial.println("Sending 180 throttle");
                      motA.write(max_[0]);
                      motB.write(max_[1]);
                      motC.write(max_[2]);
                      motD.write(max_[3]);
            break;

            // 2
            case 50 : test();
            break;
            case 51 :
                  if((pulse_duration[0]> 1800) && ( pulse_duration[1] >1800 )&& (pulse_duration[2] > 1800 )&&( pulse_duration[3]>1800)){
                      max_[0]=pulse_duration[0];
                      max_[1]=pulse_duration[1];
                      max_[2]=pulse_duration[2];
                      max_[3]=pulse_duration[3];
                      Serial.println(" max set\n");
                  }
            break;
            case 52 :
                  if((pulse_duration[0]< 1100 )&& (pulse_duration[1] <1100 )&&( pulse_duration[2] < 1100) && (pulse_duration[3]<1100)){
                      min_[0]=pulse_duration[0];
                      min_[1]=pulse_duration[1];
                      min_[2]=pulse_duration[2];
                      min_[3]=pulse_duration[3];
                      Serial.println(" min set\n");
                  }
            break;
        }
    }
}

ISR(PCINT0_vect)
{
    current_time = micros();

    // Channel 1 -------------------------------------------------
    if (PINB & B00000001) {                                        // Is input 8 high ?
        if (previous_state[CHANNEL1] == LOW) {                     // Input 8 changed from 0 to 1 (rising edge)
            previous_state[CHANNEL1] = HIGH;                       // Save current state
            timer[CHANNEL1]          = current_time;               // Start timer
        }
    } else if(previous_state[CHANNEL1] == HIGH) {                  // Input 8 changed from 1 to 0 (falling edge)
        previous_state[CHANNEL1] = LOW;                            // Save current state
        pulse_duration[CHANNEL1] = current_time - timer[CHANNEL1]; // Stop timer & calculate pulse duration
    }

    // Channel 2 -------------------------------------------------
    if (PINB & B00000010) {                                        // Is input 9 high ?
        if (previous_state[CHANNEL2] == LOW) {                     // Input 9 changed from 0 to 1 (rising edge)
            previous_state[CHANNEL2] = HIGH;                       // Save current state
            timer[CHANNEL2]          = current_time;               // Start timer
        }
    } else if(previous_state[CHANNEL2] == HIGH) {                  // Input 9 changed from 1 to 0 (falling edge)
        previous_state[CHANNEL2] = LOW;                            // Save current state
        pulse_duration[CHANNEL2] = current_time - timer[CHANNEL2]; // Stop timer & calculate pulse duration
    }

    // Channel 3 -------------------------------------------------
    if (PINB & B00000100) {                                        // Is input 10 high ?
        if (previous_state[CHANNEL3] == LOW) {                     // Input 10 changed from 0 to 1 (rising edge)
            previous_state[CHANNEL3] = HIGH;                       // Save current state
            timer[CHANNEL3]          = current_time;               // Start timer
        }
    } else if(previous_state[CHANNEL3] == HIGH) {                  // Input 10 changed from 1 to 0 (falling edge)
        previous_state[CHANNEL3] = LOW;                            // Save current state
        pulse_duration[CHANNEL3] = current_time - timer[CHANNEL3]; // Stop timer & calculate pulse duration
    }

    // Channel 4 -------------------------------------------------
    if (PINB & B00001000) {                                        // Is input 11 high ?
        if (previous_state[CHANNEL4] == LOW) {                     // Input 11 changed from 0 to 1 (rising edge)
            previous_state[CHANNEL4] = HIGH;                       // Save current state
            timer[CHANNEL4]          = current_time;               // Start timer
        }
    } else if(previous_state[CHANNEL4] == HIGH) {                  // Input 11 changed from 1 to 0 (falling edge)
        previous_state[CHANNEL4] = LOW;                            // Save current state
        pulse_duration[CHANNEL4] = current_time - timer[CHANNEL4]; // Stop timer & calculate pulse duration
    }
}

void test()
{
    for (;;) {
        Serial.print("Speed = ");
        Serial.println(pulse_duration[2]);

        motA.write(pulse_duration[2]);
        motB.write(pulse_duration[2]);
        motC.write(pulse_duration[2]);
        motD.write(pulse_duration[2]);
    }

    Serial.println("STOP");
    motA.write(0);
    motB.write(0);
    motC.write(0);
    motD.write(0);
}

void displayInstructions()
{
    Serial.println("READY - PLEASE SEND INSTRUCTIONS AS FOLLOWING :");
    Serial.println("\t0 : Sends 0 throttle");
    Serial.println("\t1 : Sends 180 throttle");
    Serial.println("\t2 : Runs test function\n");
    Serial.println("\t3 :set max\n");
    Serial.println("\t4 : set min\n");
}
