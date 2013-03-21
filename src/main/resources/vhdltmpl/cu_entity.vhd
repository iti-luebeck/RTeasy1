LIBRARY ieee;
USE ieee.std_logic_1164.ALL;

ENTITY %%COMPONENT_NAME_cu IS
  PORT(
    CLK, RESET : IN  std_logic;
    C          : OUT std_logic_vector(0 TO %%C_MAX);
    I          : IN  std_logic_vector(0 TO %%I_MAX)
  );
END %%COMPONENT_NAME_cu;