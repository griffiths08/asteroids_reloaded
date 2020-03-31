class Player extends Physics_Object {
  String Player_ID;
  boolean h = false, m=false;
  float Health, HT, RAccl=0;
  int Lives, Score, ST, LFF;
  Particle_System PF = new Particle_System(new PVector(0, 0), 2);
  Player(float x, float y) {//Sw to vector
    Is_Game_Obj= true;
    Location = new PVector(x, y);
    Velocity = new PVector(0, 0);
    Acceleration = new PVector(0, 0);
    Mass = 8;
    RMass = 4;
    Rotation = 0;
    Wrap_Offset = 0;
    Drag_Coefficient=0.04;
    RDrag_Coefficient=0.06;
    Health = 200;
    HT = 200;
    Score = 0;
    ST=0;
    LFF= frameCount;
  }
  void Update() {
    // println(Angle, RVelocity);
    if (RVelocity>3)RVelocity=3;
    if (RVelocity<-3)RVelocity=-3;
    if (abs(RVelocity)<1)RVelocity=0;
    PF.Origin=Location.copy();
    Rotation=Angle;
    if (!m) Apply_Force(Drag());
   if (Sound) th.rate(Velocity.mag()*0.5);
    Move();
    PF.Update();
    Playfield.strokeWeight(2);
    Playfield.translate(Location.x, Location.y);
    Playfield.pushMatrix();
    Playfield.rotate(radians(Rotation));
    Playfield.stroke(255);
    Playfield.line(20, 0, -5, -10);
    Playfield.line(20, 0, -5, 10);
    Playfield.line(-5, -10, -5, 10);
    if (m) {
      for (int i=0; i<10; i++) PF.addParticle(Angle+180+random(-20, 20));
      switch(1) {
      case 0: 
        Playfield.stroke(#0FFFF1);
        break;
      case 1: 
        Playfield.stroke(#FF76F6);
        break;
      case 2: 
        Playfield.stroke(#FAE923);
        break;
      }
      //Playfield.stroke(color(int(random(10, 255)), int(random(10, 255)), int(random(10, 255))));
      Playfield.line(-5, -5, -14, 0);
      Playfield.line(-5, 5, -14, 0);
      Playfield.stroke(255);
    }
    Playfield.rotate(radians(-Rotation));
    Playfield.popMatrix();
    Playfield.translate(-Location.x, -Location.y);
    Playfield.strokeWeight(1);
  }
  void mv() {
    Apply_Force(PVector.fromAngle(radians(Rotation)).mult(0.4));
  }
}