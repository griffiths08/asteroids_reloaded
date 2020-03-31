void Title_Screen() {
  if (mold!=mousePressed) {
    ch = mousePressed;
    mold = mousePressed;
  } else
    ch = false;
  scale(Scale.x, Scale.y);
  //Menu 0=start,1= rot,2=shoot,3=movest,4=move,5=move,6=main,7=local,8=network,9=Mode
  if (Menu_Ani==0) {
    L1.x=100;
    L1.y=300;
    L2.x=160;
    L2.y=400;
    pbangle=360;
    ani1=415;
    ani2=500;
  }
  Title_Shader.set("time", millis() / 2000.0); // Set Shader Time
  stroke(255);
  fill(0, 255); // Set Fill Color To White
  shader(Title_Shader); // Set Shader
  rect(0, 0, size.x, size.y); // Draw Backround
  resetShader(); // Reset Shader
  noStroke();
  if (Menu_Ani==1) pbangle-=2; 
  if (Menu_Ani==1 && pbangle<270)Menu_Ani =2;
  if (Menu_Ani==2) {
    fill(255, 60);
    rect(495, ani1, 10, 40);
    ani1 -= 6;
  }
  if (Menu_Ani==2&&ani1<200) Menu_Ani=3;
  if (Menu_Ani==3) {
    L1_Dir=PVector.random2D().mult(20);
    L2_Dir=PVector.random2D().mult(20);
    Menu_Ani = 4;
  }
  if (Menu_Ani==4) {
    L1.add(L1_Dir);
    L2.add(L2_Dir);
    if (((L1.x<-1400||L1.x> size.x+400)||(L1.y<-400||L1.y> size.y+400))&&((L2.x<-1400||L2.x> size.x+400)||(L2.y<-400||L2.y> size.y+400)))Menu_Ani = 5;
  }
  if (Menu_Ani==5)ani2-=8;
  if (Menu_Ani==5 && ani2<-100)Menu_Ani=6;
  if (Menu_Ani<6) {
    noStroke();
    fill(255, 80);
    textFont(Logo_Font); // Set font
    scale(0.75);
    float a = -random(5, 8);
    shearX(radians(a));
    text("Asteroids", L1.x, L1.y); // Draw Title
    shearX(radians(-2*a));
    text("Reloaded", L2.x, L2.y); // Draw Title
    shearX(radians(a));
    scale(1.333333333);
    translate(500, ani2);
    fill(255, 60);
    pushMatrix();
    rotate(radians(sin(radians((pbangle)))*90));
    scale(8);
    triangle(-10, -10, -10, 10, 10, 0);
    scale(0.08);
    if (Is_Button_Selected(415, 415, 165, 165)&&Menu_Ani ==0) {
      if (Cl())Menu_Ani=1;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 80);// Set Fill Color
    }
    scale(6);
    triangle(-10, -10, -10, 10, 10, 0);
    popMatrix();
    translate(-500, -ani2);
    fill(255, 60); // Set Fill Color To White
  }
  if (Menu_Ani>5) stroke(255);
  if (Menu_Ani ==6) {
    textFont(Menu_Font);
    if (Is_Button_Selected(100, 100, 100, 100)) {
      if (Cl())Menu_Ani=9;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 100, 100, 100);
    image(GamePad_Icon, 112.5, 112.5, 75, 75);
    text("Local Game", 210, 155); // Draw Button Text
    if (Is_Button_Selected(100, 220, 100, 100)) {
      if (Cl())Menu_Ani=8;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60); // Set Fill Color
    }
    rect(100, 220, 100, 100);
    image(Network_Icon, 112.5, 232.5, 75, 75);
    text("Conect to Game Network", 210, 275); // Draw Button Text
    if (Is_Button_Selected(100, 600, 100, 100)) {
      if (Cl())Menu_Ani=0;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 600, 100, 100);
    image(Back_Icon, 112.5, 612.5, 75, 75);
    text("Back", 210, 655); // Draw Button Text
  }
  if (Menu_Ani ==7) {
    //-----------------------------------
    if (Is_Button_Selected(200, 400, 100, 100)) {
      if (Cl())Start_Local_Game();
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    textFont(Menu_Font2);
    text("Q", 215, 472); // Draw Text
    textFont(Menu_Font);
    //-----------------------------------
    // shape(Keyboard, 10, 100);
    fill(255, 60);
    text("Players:", 120, 255); // Draw Text
    if (Is_Button_Selected(200, 200, 100, 100)) {
      if (Cl())Start_Local_Game();
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    textFont(Menu_Font2);
    text("1", 215, 272); // Draw Text
    textFont(Menu_Font);
    if (Is_Button_Selected(300, 200, 100, 100)) {
      if (Cl())Menu_Ani=6;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    textFont(Menu_Font2);
    text("2", 315, 272); // Draw Text
    textFont(Menu_Font);
    if (Is_Button_Selected(100, 600, 100, 100)) {
      if (Cl())Menu_Ani=6;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 600, 100, 100);
    image(Back_Icon, 112.5, 612.5, 75, 75);
    text("Back", 210, 655); // Draw Button Text
  }
  if (Menu_Ani ==8) {
    if (Is_Button_Selected(100, 600, 100, 100)) {
      if (Cl())Menu_Ani=6;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 600, 100, 100);
    image(Back_Icon, 112.5, 612.5, 75, 75);
    text("Back", 210, 655); // Draw Button Text
    if (Is_Button_Selected(260, 600, 100, 100)) {
      if (Cl()) Is_Server =true;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(260, 600, 100, 100);
    image(Back_Icon, 112.5, 612.5, 75, 75);
    text("Start Server", 380, 655); // Draw Button Text
    fill(255, 60);
    rect(150, 100, 400, 400);
    if (Is_Button_Selected(360, 600, 100, 100)) {
      if (Cl()) In_Net_Game =true;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(360, 600, 100, 100);
    image(Back_Icon, 312.5, 612.5, 75, 75);
    text("Start Client", 480, 655); // Draw Button Text
    fill(255, 60);
  }
  if (Menu_Ani == 9) {
    boolean se=false;
    //-------Back
    if (Is_Button_Selected(100, 600+40, 100, 100)) {
      se = true;
      if (Cl())Menu_Ani=6;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 600+40, 100, 100);
    image(Back_Icon, 112.5, 612.5+40, 75, 75);
    text("Back", 210, 655+40); // Draw Button Text
    //--------------------
    //-------Classic
    if (Is_Button_Selected(100, 100-20, 100, 100)) {
      se = true;
      stroke(255);
      fill(255);
      text("Classic Asteroids", 410, 520);


      if (Cl())Start_Local_Game();
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 100-20, 100, 100);
    image(Back_Icon, 112.5, 112.5-20, 75, 75);
    text("Classic Mode", 210, 155-20); // Draw Button Text
    //--------------------
    //-------VS
    if (Is_Button_Selected(100, 100+140-20, 100, 100)) {
      se = true;
      stroke(255);
      fill(255);
      text("Vs. Mode", 410, 520);
      text("2 Player Asteroids; Play against a friend", 410, 540);
      if (Cl()) {
        Game_Mode=1;
        Start_Local_Game();
      }
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 100+140-20, 100, 100);
    image(Back_Icon, 112.5, 112.5+140-20, 75, 75);
    text("Vs. Mode", 210, 155+140-20); // Draw Button Text
    //--------------------
    //-------Time Challenge
    if (Is_Button_Selected(100, 100+280-20, 100, 100)) {
      se = true;
      stroke(255);
      fill(255);
      text("Time Challenge Mode", 410, 520);
      text("Score as many points as you can in 60 seconds.", 410, 540);
      if (Cl()) {
        Game_Mode=2;
        Start_Local_Game();
      }
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 100+280-20, 100, 100);
    image(Back_Icon, 112.5, 112.5+280-20, 75, 75);
    text("Time Challenge Mode", 210, 155+280-20); // Draw Button Text
    //--------------------
    //-------Puzzle
    //if (Is_Button_Selected(100, 100+420-20, 100, 100)) {
    // se = true;
    //  stroke(255);
    //  fill(255);
    //  text("Puzzle Mode", 410, 520);
    //  if (Cl())Menu_Ani=6;
    //  fill(10, 100, 255, 150); // Set Fill Color To Blue
    //} else {
    //  fill(255, 60);// Set Fill Color
    //}
    //rect(100, 100+420-20, 100, 100);
    //image(Back_Icon, 112.5, 112.5+420-20, 75, 75);
    //text("Puzzle Mode", 210, 155+420-20); // Draw Button Text
    //--------------------
    if (!se) {
      stroke(255);
      fill(255);
      text("Score:", 410, 520);
      text("20 - Large 50 - Medium 100 - Small", 410, 540);
    }
    //-------Video
    // image(Info_Movie, 400, 80, 400, 400);
    fill(255, 60);// Set Fill Color
    rect(400, 500, 400, 250);
    //--------------------
  }
}
boolean Cl() {
  if (ch==true) {
    ch=!ch;
    return !ch;
  } else
    return ch;
}
boolean Is_Button_Selected(int x, int y, int width, int height) {
  x*=Scale.x;
  y*=Scale.y;
  width*=Scale.x;
  height*=Scale.y;
  if (mouseX >= x && mouseX <= x+width && mouseY >= y && mouseY <= y+height) { // Is Mouse Over Button
    return true;
  } else {
    return false;
  }
}