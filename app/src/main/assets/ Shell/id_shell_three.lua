function before(hook, param) 
	local ths = param:getThis()
	if ths == nil then
		return false
	end

	local commands = ths:command()
	if commands == nil then
		return false
	end

	local command = param:joinList(commands)
	if command == nil then
		return false
	end

	log("ProcessBuilder.start(" .. command .. ")")

	local fakeCommand = param:interceptCommand(command)
	if fakeCommand == nil then
		return false
	end

	local fake = param:execEcho(fakeCommand)
	if fake == nil then
		return false
	end

	log("Command replaced(" .. command .. " => " .. fakeCommand)
	param:setResult(fake)
	return true, command, fakeCommand
end