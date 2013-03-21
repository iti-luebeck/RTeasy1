-- Tri-State driver component
LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY tristate IS
  GENERIC(width : positive);
  PORT(
    ENABLE : IN  std_logic;
    INPUT  : IN  std_logic_vector(width-1 DOWNTO 0);
    OUTPUT : OUT std_logic_vector(width-1 DOWNTO 0)
  );
END tristate;

ARCHITECTURE primitive OF tristate IS
BEGIN
  OUTPUT <= INPUT WHEN ENABLE='1' ELSE (OTHERS => 'Z');
END primitive;
