-- instantiate condition buffer register
condbuf_register: dff_reg
  GENERIC MAP(width => %%I_WIDTH, triggering_edge => '%%EDGE')
  PORT MAP(CLK => CLK, RESET => RESET, INPUT => I, OUTPUT => I_BUFFERED);

-- instantiate state register
state_register: dff_reg
  GENERIC MAP(width => %%STATEWIDTH, triggering_edge => '%%EDGE')
  PORT MAP(CLK => CLK, RESET => RESET, INPUT => NEXTSTATE, OUTPUT => STATE);

-- instantiate circuit for state transition function
statetrans: %%COMPONENT_NAME_cu_statetrans_net
  PORT MAP(I => I_BUFFERED, STATE => STATE, NEXTSTATE => NEXTSTATE);

-- instantiate circuit for output function driving control signals
output: %%COMPONENT_NAME_cu_output_net
  PORT MAP(I => I_BUFFERED, STATE => STATE, C => C_SIG);

-- only drive control signals when CLK='0' to avoid driving hazards to
-- operation unit
C <= C_SIG WHEN CLK='0' ELSE (OTHERS => '0');
