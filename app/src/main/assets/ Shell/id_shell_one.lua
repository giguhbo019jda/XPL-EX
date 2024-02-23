function before(hook, param)
	local arg = param:getArgument(0)
	if arg == nil then
		return false
	end

	log("Runtime.exec(" .. arg .. ")")
	local fakeCommand = param:interceptCommand(arg)
	if fakeCommand == nil then
		return false
	end

	local fake = param:execEcho(fakeCommand)
	if fake == nil then
		return false
	end

	log("Runtime.exec(" .. arg .. " => " .. fakeCommand)
	param:setResult(fake)
	return true, arg, fakeCommand
end