-- D-Flip-Flop register component
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY dff_reg IS
  GENERIC(width : positive; triggering_edge : bit);
  PORT(
    CLK, RESET : IN  std_logic;
    INPUT      : IN  std_logic_vector(width-1 DOWNTO 0);
    OUTPUT     : OUT std_logic_vector(width-1 DOWNTO 0)
  );
END dff_reg;

ARCHITECTURE behavioural OF dff_reg IS
BEGIN
  gen_rising_edge: IF triggering_edge='1' GENERATE
    reg_proc_rising: PROCESS(CLK,RESET)
    BEGIN
      IF RESET='1' THEN OUTPUT <= (OTHERS => '0');
      ELSIF rising_edge(CLK) THEN OUTPUT <= INPUT; END IF;
    END PROCESS;
  END GENERATE;

  gen_falling_edge: IF triggering_edge='0' GENERATE
    reg_proc_falling: PROCESS(CLK,RESET)
    BEGIN
      IF RESET='1' THEN OUTPUT <= (OTHERS => '0');
      ELSIF falling_edge(CLK) THEN OUTPUT <= INPUT; END IF;
    END PROCESS;
  END GENERATE;
END behavioural;
