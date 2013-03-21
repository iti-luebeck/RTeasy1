
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY sram_cell IS
  GENERIC(width : positive);
  PORT(
    SEL, WE : IN  std_logic;
    D_IN    : IN  std_logic_vector(width-1 DOWNTO 0);
    D_OUT   : OUT std_logic_vector(width-1 DOWNTO 0)
  );
END sram_cell;

ARCHITECTURE primitive OF sram_cell IS
  SIGNAL B : std_logic_vector(width-1 DOWNTO 0);
BEGIN
  behav: PROCESS(SEL,WE,D_IN)
  BEGIN
    IF SEL='1' THEN
      IF WE='1' THEN
        B <= D_IN;
        D_OUT <= (OTHERS => 'Z');
      ELSE
        D_OUT <= B;
      END IF;
    ELSE
      D_OUT <= (OTHERS => 'Z');
    end IF;
  END PROCESS;
END primitive;
