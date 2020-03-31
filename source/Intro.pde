void Intro() {// Make Logo
  if (!init)Init_Intro();
  Intro_loop();
}
void Init_Intro() {
  init=true;
  for (int angle = 0; angle < 360; angle += 9) {
    PVector v = PVector.fromAngle(radians(angle-135));
    v.mult(100);
    circle.add(v);
    morph.add(new PVector());
  }
  for (int x = -50; x < 50; x += 10) square.add(new PVector(x, -50)); // Top of square
  for (int y = -50; y < 50; y += 10) square.add(new PVector(50, y)); // Right side
  for (int x = 50; x > -50; x -= 10) square.add(new PVector(x, 50)); // Bottom
  for (int y = 50; y > -50; y -= 10) square.add(new PVector(-50, y)); // Left side
}

void Intro_loop() {
  background(51);
  float totalDistance = 0;
  for (int i = 0; i < circle.size(); i++) {
    PVector v1;
    if (state)  v1 = circle.get(i);
    else v1 = square.get(i);
    PVector v2 = morph.get(i);
    v2.lerp(v1, 0.1);
    totalDistance += PVector.dist(v1, v2);
  }
  translate(width/2, height/2);
  strokeWeight(2);
  scale(2);
  text("By: Fred Griffiths", -20, 0);
  text("Music is CC-COM", -20, 20);
  rotate(ra3);
  ra3+=0.02;
  beginShape();
  noFill();
  stroke(255);
  for (PVector v : morph) vertex(v.x, v.y);
  endShape(CLOSE);
  stroke(255);
  fill(255);
}