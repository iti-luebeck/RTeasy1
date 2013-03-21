LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY mux IS
  GENERIC(select_width, line_width : positive);
  PORT(
    INPUT  : IN  std_logic_vector(2**select_width*line_width-1 DOWNTO 0);
    SEL    : IN  std_logic_vector(select_width-1 DOWNTO 0);
    OUTPUT : OUT std_logic_vector(line_width-1 DOWNTO 0)
  );
END mux;

ARCHITECTURE recursive OF mux IS
  SIGNAL submux_0_OUT, submux_1_OUT : std_logic_vector(line_width-1 DOWNTO 0);

  COMPONENT mux
    GENERIC(select_width, line_width : positive);
    PORT(
      INPUT  : IN  std_logic_vector(2**select_width*line_width-1 DOWNTO 0);
      SEL    : IN  std_logic_vector(select_width-1 DOWNTO 0);
      OUTPUT : OUT std_logic_vector(line_width-1 DOWNTO 0)
    );
  END COMPONENT;

  FOR ALL : mux USE ENTITY WORK.mux(recursive);
 
BEGIN
  mux2to1: IF select_width=1 GENERATE
    OUTPUT <= INPUT(2*line_width-1 DOWNTO line_width) WHEN SEL="1"
              ELSE INPUT(line_width-1 DOWNTO 0);
  END GENERATE;

  muxNto1: IF select_width>1 GENERATE
    submux_0: mux
    GENERIC MAP(select_width => select_width-1, line_width => line_width)
    PORT MAP(INPUT  => INPUT(2**(select_width-1)*line_width-1 DOWNTO 0),
             SEL    => SEL(select_width-2 DOWNTO 0),
             OUTPUT => submux_0_OUT);
          
    submux_1: mux
    GENERIC MAP(select_width => select_width-1, line_width => line_width)
    PORT MAP(INPUT  => INPUT(2**select_width*line_width-1 DOWNTO 2**(select_width-1)*line_width),
             SEL    => SEL(select_width-2 DOWNTO 0),
             OUTPUT => submux_1_OUT);

    OUTPUT <= submux_1_OUT WHEN SEL(select_width-1)='1' ELSE submux_0_OUT;
  END GENERATE;
END recursive;
