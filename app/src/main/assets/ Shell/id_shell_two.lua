function before(hook, param)
	local arg = param:getArgument(0)
	if arg == nil then
		return false
	end

	local command = param:joinArray(arg)
	if command == nil then
		return false
	end

	log("Runtime.exec(" .. command .. ")")
	local fakeCommand = param:interceptCommand(command)
	if fakeCommand == nil then
		return false
	end

	local fake = param:execEcho(fakeCommand)
	if fake == nil then
		return false
	end

	log("Runtime.exec(" .. command .. " => " .. fakeCommand)
	param:setResult(fake)
	return true, command, fakeCommand
end