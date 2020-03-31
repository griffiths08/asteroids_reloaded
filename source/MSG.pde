class Dialog extends Physics_Object {
  boolean dis = false;
  int ID = 0, Type = 0, Delay = 200;
  Dialog(PVector L, int T) {
    Size = 1;
    Rotation = 0;
    Rot_Dir = 0;
    Location = L.copy();
    Type = T;
    Velocity = PVector.random2D().mult(0);
    Acceleration = new PVector(0, 0);
    Mass = 4;
    Drag_Coefficient = 0.1;
    Wrap_Offset = -1;
  }
  void Update() {
    Move();
    if (Type == 0 && Location.x>=size.x+400) {
      dis = true;
      Paused = false;
    }
    if (Type == 1 && Location.x<-200)dis=true;
    //if (Delay==0) {
    if (Keys.hasValue("10")) {
      if (Type==0) Velocity = PVector.fromAngle(radians(0)).mult(10);
      if (Type==1) Velocity = PVector.fromAngle(radians(180)).mult(10);
      if (Type==0)  Rot_Dir =  +1.145916;
      if (Type==1)  Rot_Dir =  -1.145916;
      // Delay--;
      if (Type==2 || Type==3 || Type==4) Reset_Game=true;
    } //else if (Delay>0) Delay--;
    Render();
  }
  void Render() {
    hud.pushMatrix();
    hud.strokeWeight(1);
    hud.translate(Location.x, Location.y);
    hud.rotate(radians(Rotation));
    hud.scale(Size);
    switch(int(random(0, 3))) {
    case 0: 
      hud.stroke(#0FFFF1);
      break;
    case 1: 
      hud.stroke(#FF76F6);
      break;
    case 2: 
      hud.stroke(#FAE923);
      break;
    }
    switch(Type) {
    case 0: 
      hud.fill(#FF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(#0FFFF1, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(#FF76F6, 255);
      hud.text("Player 1", -250, -100);
      hud.text("A&D - Rotate", -250, -50);
      hud.text("W - Thrster", -250, -0);
      hud.text("Left Alt - Fire", -250, 50);
      hud.text("P - Pause", -250, 100);
      hud.text("Press Enter", -250, 150);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 1: 
      hud.fill(#FF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(#0FFFF1, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(#FF76F6, 255);
      hud.text("Player 2", -250, -100);
      hud.text("J&L - Rotate", -250, -50);
      hud.text("I - Thrster", -250, -0);
      hud.text("Right Alt - Fire", -250, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 2: 
      hud.fill(#FF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(#FAE923, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(#FF76F6, 255);
      hud.text("Game Over", -260, -100);
      hud.text("Classic Mode", -260, -50);
      hud.text("Score : "+str(Players.get(0).Score), -260, -0);
      hud.text("Press Enter", -260, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 3: 
      hud.fill(#FF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(#FAE923, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(#FF76F6, 255);
      if (Players.get(0).Score>Players.get(1).Score) {
        hud.text("Player 1 Won!", -260, -100);
        hud.text("P1 : "+str(Players.get(0).Score), -260, -50);
        hud.text("P2 : "+str(Players.get(1).Score), -260, -0);
      } else {
        hud.text("Player 2 Won!", -260, -100);
        hud.text("P1 : "+str(Players.get(0).Score), -260, -0);
        hud.text("P2 : "+str(Players.get(1).Score), -260, -50);
      }
      hud.text("Press Enter", -260, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 4: 
      hud.fill(#FF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(#FAE923, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(#FF76F6, 255);
      hud.text("Game Over", -260, -100);
      hud.text("Time Mode", -260, -50);
      hud.text("Score : "+str(Players.get(0).Score), -260, -0);
      hud.text("Press Enter", -260, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 5: 
      hud.fill(#FAE923, 200);
      hud.rect(-260+random(0, 10), -140, 640, 300);
      hud.fill(#FF76F6, 255);
      hud.rect(-300, -150, 640, 300);
      hud.textFont(Font1, 60);
      hud.fill(#FAE923, 255);
      hud.text("Paused", -260, -100);
      hud.text("Rotate : P1-AD P2-JL", -260, -50);
      hud.text("Move : P1-W P2-I", -260, -0);
      hud.text("Fire : L&R Alt Keys", -260, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    }
  }
}